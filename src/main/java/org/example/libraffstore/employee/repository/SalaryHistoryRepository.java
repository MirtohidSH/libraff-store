package org.example.libraffstore.employee.repository;

import org.example.libraffstore.employee.entity.Employee;
import org.example.libraffstore.employee.entity.SalaryHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SalaryHistoryRepository extends JpaRepository<SalaryHistory, Integer> {

    boolean existsByEmployeeAndPayPeriod(Employee employee, String currentPeriod);
}
