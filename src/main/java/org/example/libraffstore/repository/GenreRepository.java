package org.example.libraffstore.repository;

import org.example.libraffstore.entity.Genre;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GenreRepository  extends JpaRepository<Genre, Integer> {
}
