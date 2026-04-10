package org.example.libraffstore.repository;

import org.example.libraffstore.entity.GradeHistory;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GradeHistoryRepository extends JpaRepository<GradeHistory, Long> {
    @EntityGraph(attributePaths = {"employee", "gradeStructure", "position", "store"})
    List<GradeHistory> findAllWithDetails();

}