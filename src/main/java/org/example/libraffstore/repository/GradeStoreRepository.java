package org.example.libraffstore.repository;

import org.example.libraffstore.entity.GradeStore;
import org.example.libraffstore.entity.GradeStructure;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface GradeStoreRepository extends JpaRepository<GradeStore, Long> {

    @Query("SELECT gp.gradeStructure FROM GradeStore gp WHERE gp.store.id = :storeId")
    public List<GradeStructure> findAllGradesByStoreId(Long storeId);
}