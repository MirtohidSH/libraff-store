package org.example.libraffstore.repository;

import org.example.libraffstore.entity.GradePosition;
import org.example.libraffstore.entity.GradeStructure;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface GradePositionRepository extends JpaRepository<GradePosition, Long> {

    @Query("SELECT gp.gradeStructure FROM GradePosition gp WHERE gp.position.id = :positionId")
    List<GradeStructure> findAllGradesByPositionId(@Param("positionId") Long positionId);
}