package org.example.libraffstore.repository;

import org.example.libraffstore.entity.Discount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface DiscountRepository extends JpaRepository<Discount, Long> {

    @Query("""
        SELECT d FROM Discount d
        WHERE d.isActive = true
        AND d.startDate <= :today
        AND d.endDate >= :today
        AND (
            d.book.id = :bookId OR
            d.author.id IN :authorIds OR
            d.genre.id = :genreId OR
            d.store.id = :storeId
        )
        ORDER BY d.discountPercentage DESC
    """)
    List<Discount> findApplicableDiscounts(@Param("bookId") Long bookId,
                                           @Param("authorIds") List<Long> authorIds,
                                           @Param("genreId") Long genreId,
                                           @Param("storeId") Long storeId,
                                           @Param("today") LocalDate today);

    List<Discount> findByIsActiveTrue();
}