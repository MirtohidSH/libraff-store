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

    //İşçi sorğu yaradır
    @Transactional
    public BookTransferResponse createTransferRequest(BookTransferRequest request) {

        Book book = bookRepository.findById(request.getBookId())
                .orElseThrow(() -> new NotFoundException("Kitab tapılmadı. ID: " + request.getBookId()));

        Store fromStore = storeRepository.findById(request.getFromStoreId())
                .orElseThrow(() -> new NotFoundException("Mağaza tapılmadı. ID: " + request.getFromStoreId()));

        Store toStore = storeRepository.findById(request.getToStoreId())
                .orElseThrow(() -> new NotFoundException("Mağaza tapılmadı. ID: " + request.getToStoreId()));

        Employee requestedEmployee = employeeRepository.findById(request.getRequestedEmployeeId())
                .orElseThrow(() -> new NotFoundException("İşçi tapılmadı. ID: " + request.getRequestedEmployeeId()));

        // Eyni mağazaya transfer olmaz
        if (fromStore.getId().equals(toStore.getId()))
            throw new BusinessException("Kitab eyni mağazaya transfer edilə bilməz.");

        // fromStore-da bu kitabın stoku varmı və kifayət qədərmi
        StoreBookStock stock = storeBookStockRepository
                .findByBookIdAndStoreId(book.getId(), fromStore.getId())
                .orElseThrow(() -> new NotFoundException(
                        fromStore.getName() + " mağazasında bu kitab tapılmadı: " + book.getName()));

        if (stock.getQuantity() < request.getQuantity())
            throw new BusinessException(String.format(
                    "%s mağazasında yalnız %d ədəd %s var. İstənilən: %d",
                    fromStore.getName(), stock.getQuantity(), book.getName(), request.getQuantity()));

        // Sorğu yarat
        BookTransfer transfer = new BookTransfer();
        transfer.setBook(book);
        transfer.setFromStore(fromStore);
        transfer.setToStore(toStore);
        transfer.setRequestedEmployee(requestedEmployee);
        transfer.setQuantity(request.getQuantity());
        transfer.setRequestedAt(LocalDateTime.now());
        transfer.setTransferStatus(TransferStatus.PENDING);

        return toResponse(bookTransferRepository.save(transfer));
    }


    public List<BookTransferResponse> getPendingTransfers() {
        return bookTransferRepository.findByTransferStatus(TransferStatus.PENDING)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public List<StoreBookStockResponse> getAvailableStocks(Long bookId) {
        bookRepository.findById(bookId)
                .orElseThrow(() -> new NotFoundException("Kitab tapılmadı. ID: " + bookId));

        return storeBookStockRepository.findByBookId(bookId)
                .stream()
                .map(stock -> {
                    StoreBookStockResponse response = new StoreBookStockResponse();
                    response.setStoreId(stock.getStore().getId());
                    response.setStoreName(stock.getStore().getName());
                    response.setBookName(stock.getBook().getName());
                    response.setQuantity(stock.getQuantity());
                    return response;
                })
                .toList();
    }

    @Transactional
    public BookTransferResponse approveTransfer(Long transferId, Long managerId) {

        BookTransfer transfer = getTransferOrThrow(transferId);

        validatePending(transfer);
        validateManager(managerId);

        Employee manager = employeeRepository.findById(managerId)
                .orElseThrow(() -> new NotFoundException("Müdür tapılmadı. ID: " + managerId));

        // fromStore stokunu azalt
        StoreBookStock fromStock = storeBookStockRepository
                .findByBookIdAndStoreId(transfer.getBook().getId(), transfer.getFromStore().getId())
                .orElseThrow(() -> new NotFoundException("Stok tapılmadı."));

        if (fromStock.getQuantity() < transfer.getQuantity())
            throw new BusinessException("Stokda kifayət qədər kitab yoxdur. Mövcud: " + fromStock.getQuantity());

        fromStock.setQuantity(fromStock.getQuantity() - transfer.getQuantity());
        storeBookStockRepository.save(fromStock);

        // toStore stokunu artır (yoxdursa yarat)
        StoreBookStock toStock = storeBookStockRepository
                .findByBookIdAndStoreId(transfer.getBook().getId(), transfer.getToStore().getId())
                .orElseGet(() -> {
                    StoreBookStock newStock = new StoreBookStock();
                    newStock.setBook(transfer.getBook());
                    newStock.setStore(transfer.getToStore());
                    newStock.setQuantity(0);
                    return newStock;
                });

        toStock.setQuantity(toStock.getQuantity() + transfer.getQuantity());
        storeBookStockRepository.save(toStock);

        // Transfer yenilə
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
        validateManager(managerId);

        Employee manager = employeeRepository.findById(managerId)
                .orElseThrow(() -> new NotFoundException("Müdür tapılmadı. ID: " + managerId));

        transfer.setApprovedEmployee(manager);
        transfer.setApprovedAt(LocalDateTime.now());
        transfer.setTransferStatus(TransferStatus.REJECTED);

        return toResponse(bookTransferRepository.save(transfer));
    }

    private BookTransfer getTransferOrThrow(Long transferId) {
        return bookTransferRepository.findById(transferId)
                .orElseThrow(() -> new NotFoundException("Transfer tapılmadı. ID: " + transferId));
    }

    private void validatePending(BookTransfer transfer) {
        if (transfer.getTransferStatus() != TransferStatus.PENDING)
            throw new BusinessException("Bu sorğu artıq " + transfer.getTransferStatus() + " statusundadır.");
    }

    private void validateManager(Long managerId) {
        Employee manager = employeeRepository.findById(managerId)
                .orElseThrow(() -> new NotFoundException("Müdür tapılmadı. ID: " + managerId));

        if (manager.getPosition().getPositionType() != PositionType.MANAGER)
            throw new BusinessException("Yalnız müdür transfer sorğusunu təsdiqləyə/rəd edə bilər.");
    }

    private BookTransferResponse toResponse(BookTransfer transfer) {
        BookTransferResponse response = new BookTransferResponse();
        response.setId(transfer.getId());
        response.setBookName(transfer.getBook().getName());
        response.setFromStore(transfer.getFromStore().getName());
        response.setToStore(transfer.getToStore().getName());
        response.setRequestedEmployee(
                transfer.getRequestedEmployee().getFirstName() + " " +
                        transfer.getRequestedEmployee().getLastName());
        if (transfer.getApprovedEmployee() != null) {
            response.setApprovedEmployee(
                    transfer.getApprovedEmployee().getFirstName() + " " +
                            transfer.getApprovedEmployee().getLastName());
        }
        response.setQuantity(transfer.getQuantity());
        response.setRequestedAt(transfer.getRequestedAt());
        response.setApprovedAt(transfer.getApprovedAt());
        response.setCompletedAt(transfer.getCompletedAt());
        response.setTransferStatus(transfer.getTransferStatus());
        return response;
    }
}