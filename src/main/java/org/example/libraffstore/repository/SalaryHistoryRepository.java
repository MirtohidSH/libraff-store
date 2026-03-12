package org.example.libraffstore.repository;

import org.example.libraffstore.entity.Employee;
import org.example.libraffstore.entity.SalaryHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SalaryHistoryRepository extends JpaRepository<SalaryHistory, Integer> {
    SalaryHistory findById(Long id);

    boolean existsByEmployeeAndPayPeriod(Employee employee, String currentPeriod);
}