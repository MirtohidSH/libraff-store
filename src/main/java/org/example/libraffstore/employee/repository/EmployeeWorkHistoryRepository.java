package org.example.libraffstore.employee.repository;

import org.example.libraffstore.employee.entity.EmployeeWorkHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmployeeWorkHistoryRepository extends JpaRepository<EmployeeWorkHistory, Long> {

    Optional<EmployeeWorkHistory> findByEmployeeIdAndEndDateIsNull(Long employeeId);
}
