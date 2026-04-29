package org.example.libraffstore.catalog.repository;

import org.example.libraffstore.catalog.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book, Long> {
}