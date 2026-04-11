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
    private final StoreBookStockRepository storeBookStockRepository;

    @Transactional
    public BookTransferResponse createTransferRequest(BookTransferRequest request) {

        Book book = getBook(request.getBookId());
        Store fromStore = getStore(request.getFromStoreId());
        Store toStore = getStore(request.getToStoreId());
        Employee requestedEmployee = getEmployee(request.getRequestedEmployeeId());

        validateDifferentStores(fromStore, toStore);
        validateStockAvailability(book, fromStore, request.getQuantity());

        return toResponse(bookTransferRepository.save(
                buildTransfer(book, fromStore, toStore, requestedEmployee, request.getQuantity())));
    }

    public List<BookTransferResponse> getPendingTransfers() {
        return bookTransferRepository.findByTransferStatus(TransferStatus.PENDING)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public List<StoreBookStockResponse> getAvailableStocks(Long bookId) {
        getBook(bookId);
        return storeBookStockRepository.findByBookId(bookId)
                .stream()
                .map(this::toStockResponse)
                .toList();
    }

    @Transactional
    public BookTransferResponse approveTransfer(Long transferId, Long managerId) {

        BookTransfer transfer = getTransferOrThrow(transferId);
        validatePending(transfer);

        Employee manager = getAndValidateManager(managerId);

        updateStock(transfer);

        transfer.setApprovedEmployee(manager);
        transfer.setApprovedAt(LocalDateTime.now());
        transfer.setCompletedAt(LocalDateTime.now());
        transfer.setTransferStatus(TransferStatus.COMPLETED);

        return toResponse(bookTransferRepository.save(transfer));
    }

    @Transactional
    public BookTransferResponse rejectTransfer(Long transferId, Long managerId) {

        BookTransfer transfer = getTransferOrThrow(transferId);
        validatePending(transfer);

        Employee manager = getAndValidateManager(managerId);

        transfer.setApprovedEmployee(manager);
        transfer.setApprovedAt(LocalDateTime.now());
        transfer.setTransferStatus(TransferStatus.REJECTED);

        return toResponse(bookTransferRepository.save(transfer));
    }

    private void updateStock(BookTransfer transfer) {
        StoreBookStock fromStock = storeBookStockRepository.findByBookIdAndStoreId(transfer.getBook().getId(), transfer.getFromStore().getId())
                .orElseThrow(() -> new NotFoundException("Stok tapılmadı."));

        if (fromStock.getQuantity() < transfer.getQuantity())
            throw new BusinessException("Stokda kifayət qədər kitab yoxdur. Mövcud: " + fromStock.getQuantity());

        fromStock.setQuantity(fromStock.getQuantity() - transfer.getQuantity());
        storeBookStockRepository.save(fromStock);

        StoreBookStock toStock = storeBookStockRepository
                .findByBookIdAndStoreId(transfer.getBook().getId(), transfer.getToStore().getId())
                .orElseGet(() -> buildNewStock(transfer.getBook(), transfer.getToStore()));

        toStock.setQuantity(toStock.getQuantity() + transfer.getQuantity());
        storeBookStockRepository.save(toStock);
    }

    private void validateDifferentStores(Store fromStore, Store toStore) {
        if (fromStore.getId().equals(toStore.getId()))
            throw new BusinessException("Kitab eyni mağazaya transfer edilə bilməz.");
    }

    private void validateStockAvailability(Book book, Store fromStore, Integer quantity) {
        StoreBookStock stock = storeBookStockRepository
                .findByBookIdAndStoreId(book.getId(), fromStore.getId())
                .orElseThrow(() -> new NotFoundException(fromStore.getName() + " mağazasında bu kitab tapılmadı: " + book.getName()));

        if (stock.getQuantity() < quantity)
            throw new BusinessException(String.format("%s mağazasında yalnız %d ədəd %s var. İstənilən: %d",
                    fromStore.getName(), stock.getQuantity(), book.getName(), quantity));
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
        BookTransfer transfer = new BookTransfer();
        transfer.setBook(book);
        transfer.setFromStore(fromStore);
        transfer.setToStore(toStore);
        transfer.setRequestedEmployee(requestedEmployee);
        transfer.setQuantity(quantity);
        transfer.setRequestedAt(LocalDateTime.now());
        transfer.setTransferStatus(TransferStatus.PENDING);
        return transfer;
    }

    private StoreBookStock buildNewStock(Book book, Store store) {
        StoreBookStock stock = new StoreBookStock();
        stock.setBook(book);
        stock.setStore(store);
        stock.setQuantity(0);
        return stock;
    }

    private BookTransferResponse toResponse(BookTransfer transfer) {
        BookTransferResponse response = new BookTransferResponse();
        response.setId(transfer.getId());
        response.setBookName(transfer.getBook().getName());
        response.setFromStore(transfer.getFromStore().getName());
        response.setToStore(transfer.getToStore().getName());
        response.setRequestedEmployee(transfer.getRequestedEmployee().getFirstName() + " " + transfer.getRequestedEmployee().getLastName());
        if (transfer.getApprovedEmployee() != null) {
            response.setApprovedEmployee(transfer.getApprovedEmployee().getFirstName() + " " + transfer.getApprovedEmployee().getLastName());
        }
        response.setQuantity(transfer.getQuantity());
        response.setRequestedAt(transfer.getRequestedAt());
        response.setApprovedAt(transfer.getApprovedAt());
        response.setCompletedAt(transfer.getCompletedAt());
        response.setTransferStatus(transfer.getTransferStatus());
        return response;
    }

    private StoreBookStockResponse toStockResponse(StoreBookStock stock) {
        StoreBookStockResponse response = new StoreBookStockResponse();
        response.setStoreId(stock.getStore().getId());
        response.setStoreName(stock.getStore().getName());
        response.setBookName(stock.getBook().getName());
        response.setQuantity(stock.getQuantity());
        return response;
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