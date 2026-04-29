package org.example.libraffstore.catalog.repository;

import org.example.libraffstore.catalog.entity.Author;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthorRepository extends JpaRepository<Author, Long> {
}
