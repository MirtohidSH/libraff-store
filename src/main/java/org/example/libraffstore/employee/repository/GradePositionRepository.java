package org.example.libraffstore.employee.repository;

import org.example.libraffstore.employee.entity.GradePosition;
import org.example.libraffstore.employee.entity.GradeStructure;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface GradePositionRepository extends JpaRepository<GradePosition, Long> {

    @Query("SELECT gp.gradeStructure FROM GradePosition gp WHERE gp.position.id = :positionId")
    List<GradeStructure> findAllGradesByPositionId(@Param("positionId") Long positionId);
}
