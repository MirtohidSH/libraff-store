package org.example.libraffstore.service;

import lombok.RequiredArgsConstructor;
import org.example.libraffstore.dto.request.BookTransferRequest;
import org.example.libraffstore.dto.response.BookTransferResponse;
import org.example.libraffstore.dto.response.StoreBookStockResponse;
import org.example.libraffstore.entity.*;
import org.example.libraffstore.enums.PositionType;
import org.example.libraffstore.enums.TransferStatus;
import org.example.libraffstore.exception.BusinessException;
import org.example.libraffstore.exception.NotFoundException;
import org.example.libraffstore.mapper.BookTransferMapper;
import org.example.libraffstore.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookTransferService {

    private final BookTransferRepository bookTransferRepository;
    private final BookRepository bookRepository;
    private final StoreRepository storeRepository;
    private final EmployeeRepository employeeRepository;
    private final BookStockService bookStockService;
    private final BookTransferMapper bookTransferMapper;

    @Transactional
    public BookTransferResponse createTransferRequest(BookTransferRequest request) {
        Book book = getBook(request.getBookId());
        Store fromStore = getStore(request.getFromStoreId());
        Store toStore = getStore(request.getToStoreId());
        Employee requestedEmployee = getEmployee(request.getRequestedEmployeeId());

        validateDifferentStores(fromStore, toStore);
        bookStockService.validateStockAvailability(book, fromStore, request.getQuantity());

        BookTransfer transfer = buildTransfer(book, fromStore, toStore, requestedEmployee, request.getQuantity());
        return bookTransferMapper.toResponse(bookTransferRepository.save(transfer));
    }

    @Transactional(readOnly = true)
    public List<BookTransferResponse> getPendingTransfers() {
        return bookTransferRepository.findByTransferStatus(TransferStatus.PENDING)
                .stream()
                .map(bookTransferMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<StoreBookStockResponse> getAvailableStocks(Long bookId) {
        return bookStockService.getAvailableStocks(bookId);
    }

    @Transactional
    public BookTransferResponse approveTransfer(Long transferId, Long managerId) {
        BookTransfer transfer = getTransferOrThrow(transferId);
        validatePending(transfer);

        Employee manager = getAndValidateManager(managerId);
        bookStockService.updateStock(transfer);

        transfer.setApprovedEmployee(manager);
        transfer.setApprovedAt(LocalDateTime.now());
        transfer.setCompletedAt(LocalDateTime.now());
        transfer.setTransferStatus(TransferStatus.COMPLETED);

        return bookTransferMapper.toResponse(bookTransferRepository.save(transfer));
    }

    @Transactional
    public BookTransferResponse rejectTransfer(Long transferId, Long managerId) {
        BookTransfer transfer = getTransferOrThrow(transferId);
        validatePending(transfer);

        Employee manager = getAndValidateManager(managerId);

        transfer.setApprovedEmployee(manager);
        transfer.setApprovedAt(LocalDateTime.now());
        transfer.setTransferStatus(TransferStatus.REJECTED);

        return bookTransferMapper.toResponse(bookTransferRepository.save(transfer));
    }

    private void validateDifferentStores(Store fromStore, Store toStore) {
        if (fromStore.getId().equals(toStore.getId()))
            throw new BusinessException("Kitab eyni mağazaya transfer edilə bilməz.");
    }

    private void validatePending(BookTransfer transfer) {
        if (transfer.getTransferStatus() != TransferStatus.PENDING)
            throw new BusinessException("Bu sorğu artıq " + transfer.getTransferStatus() + " statusundadır.");
    }

    private Employee getAndValidateManager(Long managerId) {
        Employee manager = getEmployee(managerId);
        if (manager.getPosition().getPositionType() != PositionType.MANAGER)
            throw new BusinessException("Yalnız müdür transfer sorğusunu təsdiqləyə/rəd edə bilər.");
        return manager;
    }

    private BookTransfer buildTransfer(Book book, Store fromStore, Store toStore, Employee requestedEmployee, Integer quantity) {
        return BookTransfer.builder()
                .book(book)
                .fromStore(fromStore)
                .toStore(toStore)
                .requestedEmployee(requestedEmployee)
                .quantity(quantity)
                .requestedAt(LocalDateTime.now())
                .transferStatus(TransferStatus.PENDING)
                .build();
    }

    private BookTransfer getTransferOrThrow(Long transferId) {
        return bookTransferRepository.findById(transferId)
                .orElseThrow(() -> new NotFoundException("Transfer tapılmadı. ID: " + transferId));
    }

    private Book getBook(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Kitab tapılmadı. ID: " + id));
    }

    private Store getStore(Long id) {
        return storeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Mağaza tapılmadı. ID: " + id));
    }

    private Employee getEmployee(Long id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("İşçi tapılmadı. ID: " + id));
    }
}