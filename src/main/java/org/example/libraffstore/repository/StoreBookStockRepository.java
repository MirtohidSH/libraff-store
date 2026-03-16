package org.example.libraffstore.repository;

import org.example.libraffstore.entity.StoreBookStock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StoreBookStockRepository extends JpaRepository<StoreBookStock, Long> {

    Optional<StoreBookStock> findByBookIdAndStoreId(Long bookId, Long storeId);

    List<StoreBookStock> findByBookId(Long bookId);
}
