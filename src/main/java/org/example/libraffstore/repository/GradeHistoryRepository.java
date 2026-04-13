package org.example.libraffstore.repository;

import org.example.libraffstore.entity.GradeHistory;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GradeHistoryRepository extends JpaRepository<GradeHistory, Long> {

    @EntityGraph(attributePaths = {"employee", "gradeStructure", "position", "store"})
    List<GradeHistory> findAll();
}