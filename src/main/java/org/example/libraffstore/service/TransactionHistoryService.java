package org.example.libraffstore.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.libraffstore.entity.Employee;
import org.example.libraffstore.entity.StoreBookStock;
import org.example.libraffstore.entity.TransactionHistory;
import org.example.libraffstore.exception.BusinessException;
import org.example.libraffstore.exception.NotFoundException;
import org.example.libraffstore.repository.StoreBookStockRepository;
import org.example.libraffstore.repository.EmployeeRepository;
import org.example.libraffstore.repository.TransactionHistoryRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class TransactionHistoryService {

    private final StoreBookStockRepository storeBookStockRepository;
    private final TransactionHistoryRepository historyRepository;
    private final EmployeeRepository employeeRepository;
    private final DiscountService discountService;


    @Transactional
    public void sellBook(Map<Long, Long> soldBooks, Long storeId, Long employeeId) {

        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new NotFoundException("İşçi tapılmadı"));

        for (Map.Entry<Long, Long> entry : soldBooks.entrySet()) {
            Long bookId = entry.getKey();
            Long quantityToSell = entry.getValue();

            StoreBookStock stock = storeBookStockRepository.findByBookIdAndStoreIdWithDetails(bookId, storeId)
                    .orElseThrow(() -> new NotFoundException("Kitab bu store-da tapılmadı"));

            if (stock.getQuantity() < quantityToSell)
                throw new BusinessException("Stokda kifayət qədər kitab yoxdur: " + stock.getBook().getName());

            stock.setQuantity((int) (stock.getQuantity() - quantityToSell));
            storeBookStockRepository.save(stock);

            BigDecimal finalPrice = discountService.calculateDiscountedPrice(stock.getBook(), storeId);

            TransactionHistory history = new TransactionHistory();
            history.setBook(stock.getBook());
            history.setQuantity(Math.toIntExact(quantityToSell));
            history.setSalesPrice(finalPrice);
            history.setPurchasePrice(stock.getBook().getPurchasePrice());
            history.setStore(stock.getStore());
            history.setTransactionDate(java.time.LocalDate.now());
            history.setEmployee(employee);

            historyRepository.save(history);
        }
    }
}