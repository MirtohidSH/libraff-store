package org.example.libraffstore.inventory.service;

import org.example.libraffstore.catalog.entity.Book;
import org.example.libraffstore.common.exception.BusinessException;
import org.example.libraffstore.common.exception.NotFoundException;
import org.example.libraffstore.inventory.entity.BookTransfer;
import org.example.libraffstore.inventory.entity.Store;
import org.example.libraffstore.inventory.entity.StoreBookStock;
import org.example.libraffstore.inventory.mapper.BookTransferMapper;
import org.example.libraffstore.catalog.repository.BookRepository;
import org.example.libraffstore.inventory.repository.StoreBookStockRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookStockServiceTest {

    @Mock
    private StoreBookStockRepository storeBookStockRepository;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private BookTransferMapper bookTransferMapper;

    @InjectMocks
    private BookStockService bookStockService;

    private Book book;
    private Store fromStore;
    private Store toStore;

    @BeforeEach
    void setUp() {
        book = new Book();
        book.setId(1L);
        book.setName("Test Book");

        fromStore = new Store();
        fromStore.setId(10L);
        fromStore.setName("Store A");

        toStore = new Store();
        toStore.setId(20L);
        toStore.setName("Store B");
    }

    // ── validateStockAvailability ──────────────────────────────────────────────

    @Test
    void validateStockAvailability_whenStockNotFound_shouldThrowNotFoundException() {
        when(storeBookStockRepository.findByBookIdAndStoreId(book.getId(), fromStore.getId()))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookStockService.validateStockAvailability(book, fromStore, 3))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void validateStockAvailability_whenQuantityExceedsStock_shouldThrowBusinessException() {
        StoreBookStock stock = StoreBookStock.builder()
                .book(book).store(fromStore).quantity(2).build();

        when(storeBookStockRepository.findByBookIdAndStoreId(book.getId(), fromStore.getId()))
                .thenReturn(Optional.of(stock));

        assertThatThrownBy(() -> bookStockService.validateStockAvailability(book, fromStore, 5))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    void validateStockAvailability_whenStockSufficient_shouldNotThrow() {
        StoreBookStock stock = StoreBookStock.builder()
                .book(book).store(fromStore).quantity(10).build();

        when(storeBookStockRepository.findByBookIdAndStoreId(book.getId(), fromStore.getId()))
                .thenReturn(Optional.of(stock));

        assertThatCode(() -> bookStockService.validateStockAvailability(book, fromStore, 10))
                .doesNotThrowAnyException();
    }

    @Test
    void validateStockAvailability_whenExactQuantity_shouldNotThrow() {
        StoreBookStock stock = StoreBookStock.builder()
                .book(book).store(fromStore).quantity(5).build();

        when(storeBookStockRepository.findByBookIdAndStoreId(book.getId(), fromStore.getId()))
                .thenReturn(Optional.of(stock));

        assertThatCode(() -> bookStockService.validateStockAvailability(book, fromStore, 5))
                .doesNotThrowAnyException();
    }

    // ── updateStock ───────────────────────────────────────────────────────────

    @Test
    void updateStock_whenFromStockNotFound_shouldThrowNotFoundException() {
        BookTransfer transfer = buildTransfer(3);
        when(storeBookStockRepository.findByBookIdAndStoreId(book.getId(), fromStore.getId()))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookStockService.updateStock(transfer))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void updateStock_whenFromStockInsufficient_shouldThrowBusinessException() {
        BookTransfer transfer = buildTransfer(10);

        StoreBookStock fromStock = StoreBookStock.builder()
                .book(book).store(fromStore).quantity(2).build();
        when(storeBookStockRepository.findByBookIdAndStoreId(book.getId(), fromStore.getId()))
                .thenReturn(Optional.of(fromStock));

        assertThatThrownBy(() -> bookStockService.updateStock(transfer))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    void updateStock_whenToStockExists_shouldDecrementFromAndIncrementTo() {
        BookTransfer transfer = buildTransfer(3);

        StoreBookStock fromStock = StoreBookStock.builder()
                .book(book).store(fromStore).quantity(10).build();
        StoreBookStock toStock = StoreBookStock.builder()
                .book(book).store(toStore).quantity(5).build();

        when(storeBookStockRepository.findByBookIdAndStoreId(book.getId(), fromStore.getId()))
                .thenReturn(Optional.of(fromStock));
        when(storeBookStockRepository.findByBookIdAndStoreId(book.getId(), toStore.getId()))
                .thenReturn(Optional.of(toStock));

        bookStockService.updateStock(transfer);

        assertThat(fromStock.getQuantity()).isEqualTo(7);
        assertThat(toStock.getQuantity()).isEqualTo(8);
        verify(storeBookStockRepository, times(2)).save(any(StoreBookStock.class));
    }

    @Test
    void updateStock_whenToStockDoesNotExist_shouldCreateNewStockRecord() {
        BookTransfer transfer = buildTransfer(4);

        StoreBookStock fromStock = StoreBookStock.builder()
                .book(book).store(fromStore).quantity(10).build();

        when(storeBookStockRepository.findByBookIdAndStoreId(book.getId(), fromStore.getId()))
                .thenReturn(Optional.of(fromStock));
        when(storeBookStockRepository.findByBookIdAndStoreId(book.getId(), toStore.getId()))
                .thenReturn(Optional.empty());

        bookStockService.updateStock(transfer);

        assertThat(fromStock.getQuantity()).isEqualTo(6);
        verify(storeBookStockRepository, times(2)).save(any(StoreBookStock.class));
    }

    // ── helpers ───────────────────────────────────────────────────────────────

    private BookTransfer buildTransfer(int quantity) {
        return BookTransfer.builder()
                .book(book)
                .fromStore(fromStore)
                .toStore(toStore)
                .quantity(quantity)
                .build();
    }
}

