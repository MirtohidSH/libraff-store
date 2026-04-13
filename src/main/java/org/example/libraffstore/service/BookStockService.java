package org.example.libraffstore.service;

import lombok.RequiredArgsConstructor;
import org.example.libraffstore.dto.response.StoreBookStockResponse;
import org.example.libraffstore.entity.*;
import org.example.libraffstore.exception.BusinessException;
import org.example.libraffstore.exception.NotFoundException;
import org.example.libraffstore.mapper.BookTransferMapper;
import org.example.libraffstore.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookStockService {

    private final StoreBookStockRepository storeBookStockRepository;
    private final BookRepository bookRepository;
    private final BookTransferMapper bookTransferMapper;

    @Transactional(readOnly = true)
    public List<StoreBookStockResponse> getAvailableStocks(Long bookId) {
        getBook(bookId);
        return storeBookStockRepository.findByBookId(bookId)
                .stream()
                .map(bookTransferMapper::toStockResponse)
                .toList();
    }

    public void validateStockAvailability(Book book, Store fromStore, Integer quantity) {
        StoreBookStock stock = storeBookStockRepository
                .findByBookIdAndStoreId(book.getId(), fromStore.getId())
                .orElseThrow(() -> new NotFoundException(
                        fromStore.getName() + " mağazasında bu kitab tapılmadı: " + book.getName()));

        if (stock.getQuantity() < quantity)
            throw new BusinessException(String.format("%s mağazasında yalnız %d ədəd %s var. İstənilən: %d",
                    fromStore.getName(), stock.getQuantity(), book.getName(), quantity));
    }

    public void updateStock(BookTransfer transfer) {
        StoreBookStock fromStock = storeBookStockRepository
                .findByBookIdAndStoreId(transfer.getBook().getId(), transfer.getFromStore().getId())
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

    private StoreBookStock buildNewStock(Book book, Store store) {
        return StoreBookStock.builder()
                .book(book)
                .store(store)
                .quantity(0)
                .build();
    }

    private Book getBook(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Kitab tapılmadı. ID: " + id));
    }
}