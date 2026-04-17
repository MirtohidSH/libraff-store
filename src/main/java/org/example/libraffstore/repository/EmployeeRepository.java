package org.example.libraffstore.repository;

import org.example.libraffstore.entity.Employee;
import org.example.libraffstore.enums.PositionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    Page<Employee> findAllByIsActiveTrue(Pageable pageable);

    List<Employee> findAllByIsActiveTrue();

    @EntityGraph(attributePaths = {"store", "position"})
    Optional<Employee> findWithDetailsById(Long id);

    Optional<Employee> findByFIN(String FIN);
    long countByStoreIdAndPositionPositionTypeAndIsActiveTrue(Long storeId, PositionType positionType);
    boolean existsByFIN(String FIN);
    boolean existsByEmail(String email);
    boolean existsByPhone(String phone);
    boolean existsByFINAndIdNot(String FIN, Long id);
    boolean existsByEmailAndIdNot(String email, Long id);
    boolean existsByPhoneAndIdNot(String phone, Long id);
}