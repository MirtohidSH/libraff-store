package org.example.libraffstore.repository;

import org.example.libraffstore.entity.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    Page<Employee> findAllByIsActiveTrue(Pageable pageable);

    List<Employee> findAllByIsActiveTrue();

    Optional<Employee> findByFIN(String FIN);
    boolean existsByFIN(String FIN);
    boolean existsByEmail(String email);
    boolean existsByPhone(String phone);
    boolean existsByFINAndIdNot(String FIN, Long id);
    boolean existsByEmailAndIdNot(String email, Long id);
    boolean existsByPhoneAndIdNot(String phone, Long id);
}