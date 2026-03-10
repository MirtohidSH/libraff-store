package org.example.libraffstore.repository;

import org.example.libraffstore.entity.Position;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PositionRepository extends JpaRepository<Position, Long> {
}