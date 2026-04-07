package org.example.libraffstore.repository;

import org.example.libraffstore.entity.GradeHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GradeHistoryRepository extends JpaRepository<GradeHistory, Long> {
    @Query("SELECT gh FROM GradeHistory gh " +
            "LEFT JOIN FETCH gh.employee " +
            "LEFT JOIN FETCH gh.gradeStructure " +
            "LEFT JOIN FETCH gh.position " +
            "LEFT JOIN FETCH gh.store " +
            "WHERE 1=1")
    List<GradeHistory> findAllWithDetails();

}