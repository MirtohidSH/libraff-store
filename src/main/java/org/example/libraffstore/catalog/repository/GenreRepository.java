package org.example.libraffstore.catalog.repository;

import org.example.libraffstore.catalog.entity.Genre;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GenreRepository extends JpaRepository<Genre, Long> {
}
