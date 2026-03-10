package org.example.libraffstore.repository;

import org.example.libraffstore.entity.GradeHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GradeHistoryRepository extends JpaRepository<GradeHistory, Long> {
}