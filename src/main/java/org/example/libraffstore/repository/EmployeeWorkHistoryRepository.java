package org.example.libraffstore.repository;

import org.example.libraffstore.entity.EmployeeWorkHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeWorkHistoryRepository extends JpaRepository<EmployeeWorkHistory, Long> {
}