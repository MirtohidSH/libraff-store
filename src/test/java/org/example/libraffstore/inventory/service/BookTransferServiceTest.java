package org.example.libraffstore.inventory.service;

import org.example.libraffstore.catalog.repository.BookRepository;
import org.example.libraffstore.catalog.entity.Book;
import org.example.libraffstore.employee.entity.Employee;
import org.example.libraffstore.employee.entity.Position;
import org.example.libraffstore.employee.repository.EmployeeRepository;
import org.example.libraffstore.inventory.dto.BookTransferRequest;
import org.example.libraffstore.inventory.dto.BookTransferResponse;
import org.example.libraffstore.inventory.entity.BookTransfer;
import org.example.libraffstore.inventory.entity.Store;
import org.example.libraffstore.enums.PositionType;
import org.example.libraffstore.enums.TransferStatus;
import org.example.libraffstore.common.exception.BusinessException;
import org.example.libraffstore.common.exception.NotFoundException;
import org.example.libraffstore.inventory.mapper.BookTransferMapper;
import org.example.libraffstore.inventory.repository.BookTransferRepository;
import org.example.libraffstore.inventory.repository.StoreRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookTransferServiceTest {

    @Mock private BookTransferRepository bookTransferRepository;
    @Mock private BookRepository bookRepository;
    @Mock private StoreRepository storeRepository;
    @Mock private EmployeeRepository employeeRepository;
    @Mock private BookStockService bookStockService;
    @Mock private BookTransferMapper bookTransferMapper;

    @InjectMocks
    private BookTransferService bookTransferService;

    private Book book;
    private Store fromStore;
    private Store toStore;
    private Employee employee;
    private Employee manager;

    @BeforeEach
    void setUp() {
        book = new Book();
        book.setId(1L);
        book.setName("Clean Code");

        fromStore = new Store();
        fromStore.setId(10L);
        fromStore.setName("Store A");

        toStore = new Store();
        toStore.setId(20L);
        toStore.setName("Store B");

        employee = new Employee();
        employee.setId(5L);

        Position managerPosition = new Position();
        managerPosition.setId(1L);
        managerPosition.setPositionType(PositionType.MANAGER);

        manager = new Employee();
        manager.setId(99L);
        manager.setPosition(managerPosition);
    }

    // ── createTransferRequest ──────────────────────────────────────────────────

    @Test
    void createTransferRequest_whenValidRequest_shouldSaveAndReturnResponse() {
        BookTransferRequest request = buildRequest(10L, 20L, 3);

        BookTransfer saved = buildTransfer(TransferStatus.PENDING);
        BookTransferResponse response = new BookTransferResponse();

        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(storeRepository.findById(10L)).thenReturn(Optional.of(fromStore));
        when(storeRepository.findById(20L)).thenReturn(Optional.of(toStore));
        when(employeeRepository.findById(5L)).thenReturn(Optional.of(employee));
        when(bookTransferRepository.save(any())).thenReturn(saved);
        when(bookTransferMapper.toResponse(saved)).thenReturn(response);

        BookTransferResponse result = bookTransferService.createTransferRequest(request);

        assertThat(result).isEqualTo(response);
        verify(bookStockService).validateStockAvailability(book, fromStore, 3);
        verify(bookTransferRepository).save(any(BookTransfer.class));
    }

    @Test
    void createTransferRequest_whenSameStore_shouldThrowBusinessException() {
        BookTransferRequest request = buildRequest(10L, 10L, 3); // same store

        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(storeRepository.findById(10L)).thenReturn(Optional.of(fromStore));
        when(employeeRepository.findById(5L)).thenReturn(Optional.of(employee));

        assertThatThrownBy(() -> bookTransferService.createTransferRequest(request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("eyni mağazaya");
    }

    @Test
    void createTransferRequest_whenBookNotFound_shouldThrowNotFoundException() {
        BookTransferRequest request = buildRequest(10L, 20L, 3);
        when(bookRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookTransferService.createTransferRequest(request))
                .isInstanceOf(NotFoundException.class);
    }

    // ── getPendingTransfers ────────────────────────────────────────────────────

    @Test
    void getPendingTransfers_shouldReturnMappedPendingTransfers() {
        BookTransfer transfer = buildTransfer(TransferStatus.PENDING);
        BookTransferResponse response = new BookTransferResponse();

        when(bookTransferRepository.findByTransferStatus(TransferStatus.PENDING))
                .thenReturn(List.of(transfer));
        when(bookTransferMapper.toResponse(transfer)).thenReturn(response);

        List<BookTransferResponse> result = bookTransferService.getPendingTransfers();

        assertThat(result).hasSize(1).containsExactly(response);
    }

    @Test
    void getPendingTransfers_whenNoPendingTransfers_shouldReturnEmptyList() {
        when(bookTransferRepository.findByTransferStatus(TransferStatus.PENDING))
                .thenReturn(List.of());

        assertThat(bookTransferService.getPendingTransfers()).isEmpty();
    }

    // ── approveTransfer ────────────────────────────────────────────────────────

    @Test
    void approveTransfer_whenValidRequest_shouldCompleteTransferAndReturnResponse() {
        BookTransfer transfer = buildTransfer(TransferStatus.PENDING);
        BookTransferResponse response = new BookTransferResponse();

        when(bookTransferRepository.findById(1L)).thenReturn(Optional.of(transfer));
        when(employeeRepository.findById(99L)).thenReturn(Optional.of(manager));
        when(bookTransferRepository.save(transfer)).thenReturn(transfer);
        when(bookTransferMapper.toResponse(transfer)).thenReturn(response);

        BookTransferResponse result = bookTransferService.approveTransfer(1L, 99L);

        assertThat(result).isEqualTo(response);
        assertThat(transfer.getTransferStatus()).isEqualTo(TransferStatus.COMPLETED);
        verify(bookStockService).updateStock(transfer);
    }

    @Test
    void approveTransfer_whenTransferNotPending_shouldThrowBusinessException() {
        BookTransfer transfer = buildTransfer(TransferStatus.COMPLETED);
        when(bookTransferRepository.findById(1L)).thenReturn(Optional.of(transfer));

        assertThatThrownBy(() -> bookTransferService.approveTransfer(1L, 99L))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("COMPLETED");
    }

    @Test
    void approveTransfer_whenEmployeeIsNotManager_shouldThrowBusinessException() {
        BookTransfer transfer = buildTransfer(TransferStatus.PENDING);

        Position nonManagerPosition = new Position();
        nonManagerPosition.setPositionType(PositionType.CASHIER);
        Employee cashier = new Employee();
        cashier.setId(10L);
        cashier.setPosition(nonManagerPosition);

        when(bookTransferRepository.findById(1L)).thenReturn(Optional.of(transfer));
        when(employeeRepository.findById(10L)).thenReturn(Optional.of(cashier));

        assertThatThrownBy(() -> bookTransferService.approveTransfer(1L, 10L))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("müdür");
    }

    // ── rejectTransfer ─────────────────────────────────────────────────────────

    @Test
    void rejectTransfer_whenValidRequest_shouldRejectTransferAndReturnResponse() {
        BookTransfer transfer = buildTransfer(TransferStatus.PENDING);
        BookTransferResponse response = new BookTransferResponse();

        when(bookTransferRepository.findById(1L)).thenReturn(Optional.of(transfer));
        when(employeeRepository.findById(99L)).thenReturn(Optional.of(manager));
        when(bookTransferRepository.save(transfer)).thenReturn(transfer);
        when(bookTransferMapper.toResponse(transfer)).thenReturn(response);

        BookTransferResponse result = bookTransferService.rejectTransfer(1L, 99L);

        assertThat(result).isEqualTo(response);
        assertThat(transfer.getTransferStatus()).isEqualTo(TransferStatus.REJECTED);
        verifyNoInteractions(bookStockService);
    }

    @Test
    void rejectTransfer_whenTransferAlreadyRejected_shouldThrowBusinessException() {
        BookTransfer transfer = buildTransfer(TransferStatus.REJECTED);
        when(bookTransferRepository.findById(1L)).thenReturn(Optional.of(transfer));

        assertThatThrownBy(() -> bookTransferService.rejectTransfer(1L, 99L))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("REJECTED");
    }

    // ── helpers ───────────────────────────────────────────────────────────────

    private BookTransferRequest buildRequest(Long fromStoreId, Long toStoreId, int quantity) {
        BookTransferRequest req = new BookTransferRequest();
        req.setBookId(1L);
        req.setFromStoreId(fromStoreId);
        req.setToStoreId(toStoreId);
        req.setRequestedEmployeeId(5L);
        req.setQuantity(quantity);
        return req;
    }

    private BookTransfer buildTransfer(TransferStatus status) {
        return BookTransfer.builder()
                .id(1L)
                .book(book)
                .fromStore(fromStore)
                .toStore(toStore)
                .requestedEmployee(employee)
                .quantity(3)
                .requestedAt(LocalDateTime.now())
                .transferStatus(status)
                .build();
    }
}

