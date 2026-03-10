package org.example.libraffstore.repository;

import org.example.libraffstore.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompanyRepository extends JpaRepository<Company, Long> {
}