package org.example.libraffstore.repository;

import org.example.libraffstore.entity.TransactionHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface TransactionHistoryRepository extends JpaRepository<TransactionHistory, Long> {

    // 1. Get total sales for a specific STORE within a date range
    @Query("SELECT COALESCE(SUM(t.salesPrice), 0) FROM TransactionHistory t " + "WHERE t.store.id = :storeId "
            + "AND t.transactionDate BETWEEN :startDate AND :endDate")
    BigDecimal getTotalSalesByStoreAndDateRange(@Param("storeId") Long storeId, @Param("startDate") LocalDate startDate,
                                            @Param("endDate") LocalDate endDate);

    // 2. Get total sales for a specific EMPLOYEE within a date range
    @Query("SELECT COALESCE(SUM(t.salesPrice), 0) FROM TransactionHistory t "
            + "WHERE t.employee.id = :employeeId " + "AND t.transactionDate BETWEEN :startDate AND :endDate")
    BigDecimal getTotalSalesByEmployeeAndDateRange(@Param("employeeId") Long employeeId,
                                               @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}