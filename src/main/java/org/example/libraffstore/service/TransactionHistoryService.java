package org.example.libraffstore.service;

import lombok.RequiredArgsConstructor;
import org.example.libraffstore.entity.Employee;
import org.example.libraffstore.entity.StoreBookStock;
import org.example.libraffstore.entity.TransactionHistory;
import org.example.libraffstore.exception.BusinessException;
import org.example.libraffstore.exception.NotFoundException;
import org.example.libraffstore.repository.EmployeeRepository;
import org.example.libraffstore.repository.StoreBookStockRepository;
import org.example.libraffstore.repository.TransactionHistoryRepository;
import org.example.libraffstore.service.helper.DiscountPriceCalculator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class TransactionHistoryService {

    private final StoreBookStockRepository storeBookStockRepository;
    private final TransactionHistoryRepository historyRepository;
    private final EmployeeRepository employeeRepository;
    private final DiscountPriceCalculator discountPriceCalculator;

    @Transactional
    public void sellBook(Map<Long, Long> soldBooks, Long storeId, Long employeeId) {
        if (soldBooks == null || soldBooks.isEmpty())
            throw new BusinessException("Satış üçün kitab göndərilməyib.");

        Employee employee = getEmployee(employeeId);

        soldBooks.forEach((bookId, quantity) ->
                processSale(bookId, Math.toIntExact(quantity), storeId, employee));
    }

    private void processSale(Long bookId, int quantity, Long storeId, Employee employee) {
        StoreBookStock stock = storeBookStockRepository
                .findByBookIdAndStoreIdWithDetails(bookId, storeId)
                .orElseThrow(() -> new NotFoundException(
                        "Kitab bu mağazada tapılmadı. Book ID: " + bookId));

        validateStock(stock, quantity);
        stock.setQuantity(stock.getQuantity() - quantity);  // dirty checking — save() lazım deyil

        BigDecimal finalPrice = discountPriceCalculator.calculate(stock.getBook(), storeId);  // düzəldildi

        historyRepository.save(buildHistory(stock, quantity, finalPrice, employee));
    }

    private void validateStock(StoreBookStock stock, int quantityToSell) {
        if (stock.getQuantity() < quantityToSell)
            throw new BusinessException(String.format(
                    "Stokda kifayət qədər kitab yoxdur: %s. Mövcud: %d, İstənilən: %d",
                    stock.getBook().getName(), stock.getQuantity(), quantityToSell));
    }

    private TransactionHistory buildHistory(StoreBookStock stock, int quantity,
                                            BigDecimal finalPrice, Employee employee) {
        TransactionHistory history = new TransactionHistory();
        history.setBook(stock.getBook());
        history.setQuantity(quantity);
        history.setSalesPrice(finalPrice);
        history.setPurchasePrice(stock.getBook().getPurchasePrice());
        history.setStore(stock.getStore());
        history.setTransactionDate(LocalDate.now());
        history.setEmployee(employee);
        return history;
    }

    private Employee getEmployee(Long id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("İşçi tapılmadı. ID: " + id));
    }
}