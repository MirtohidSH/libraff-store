package org.example.libraffstore.repository;

import org.example.libraffstore.entity.StoreBookStock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StoreBookStockRepository extends JpaRepository<StoreBookStock, Long> {

    Optional<StoreBookStock> findByBookIdAndStoreId(Long bookId, Long storeId);

    List<StoreBookStock> findByBookId(Long bookId);
    @Query("SELECT s FROM StoreBookStock s " +
            "LEFT JOIN FETCH s.book b " +
            "LEFT JOIN FETCH b.genre " +
            "LEFT JOIN FETCH b.authors " +
            "WHERE s.book.id = :bookId AND s.store.id = :storeId")
    Optional<StoreBookStock> findByBookIdAndStoreIdWithDetails(
            @Param("bookId") Long bookId,
            @Param("storeId") Long storeId);
}
