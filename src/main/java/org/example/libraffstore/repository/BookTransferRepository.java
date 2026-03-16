package org.example.libraffstore.repository;

import org.example.libraffstore.entity.BookTransfer;
import org.example.libraffstore.enums.TransferStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookTransferRepository extends JpaRepository<BookTransfer, Long> {

    List<BookTransfer> findByTransferStatus(TransferStatus status);

    List<BookTransfer> findByToStoreIdAndTransferStatus(Long toStoreId, TransferStatus status);
}