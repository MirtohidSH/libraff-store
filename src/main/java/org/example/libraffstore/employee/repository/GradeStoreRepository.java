package org.example.libraffstore.employee.repository;

import org.example.libraffstore.employee.entity.GradeStore;
import org.example.libraffstore.employee.entity.GradeStructure;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface GradeStoreRepository extends JpaRepository<GradeStore, Long> {

    @Query("SELECT gp.gradeStructure FROM GradeStore gp WHERE gp.store.id = :storeId")
    List<GradeStructure> findAllGradesByStoreId(Long storeId);
}
