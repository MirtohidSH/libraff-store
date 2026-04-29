package org.example.libraffstore.employee.repository;

import org.example.libraffstore.employee.entity.Position;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PositionRepository extends JpaRepository<Position, Long> {
}
