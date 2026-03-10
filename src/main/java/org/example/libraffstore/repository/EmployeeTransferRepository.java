package org.example.libraffstore.repository;

import org.example.libraffstore.entity.EmployeeTransfer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeTransferRepository extends JpaRepository<EmployeeTransfer, Long> {
}