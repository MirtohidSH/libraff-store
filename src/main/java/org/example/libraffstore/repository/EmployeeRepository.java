package org.example.libraffstore.repository;

import org.example.libraffstore.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    List<Employee> findByIsActive(Boolean isActive);

    boolean existsByFIN(String FIN);
    boolean existsByEmail(String email);
    boolean existsByPhone(String phone);

    List<Employee> findAllByIsActiveTrue();
}
