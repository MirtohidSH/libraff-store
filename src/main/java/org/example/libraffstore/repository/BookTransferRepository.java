package org.example.libraffstore.repository;

import org.example.libraffstore.entity.BookTransfer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookTransferRepository extends JpaRepository<BookTransfer, Long> {
}