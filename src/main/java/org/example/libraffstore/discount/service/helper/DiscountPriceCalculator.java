package org.example.libraffstore.discount.service.helper;

import lombok.RequiredArgsConstructor;
import org.example.libraffstore.catalog.entity.Author;
import org.example.libraffstore.catalog.entity.Book;
import org.example.libraffstore.discount.entity.Discount;
import org.example.libraffstore.discount.repository.DiscountRepository;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DiscountPriceCalculator {

    private final DiscountRepository discountRepository;

    public BigDecimal calculate(Book book, Long storeId) {
        List<Long> authorIds = extractAuthorIds(book);
        Long genreId = book.getGenre() != null ? book.getGenre().getId() : null;

        List<Discount> discounts = discountRepository.findApplicableDiscounts(
                book.getId(), authorIds, genreId, storeId, LocalDate.now()
        );

        if (CollectionUtils.isEmpty(discounts)) {
            return book.getSalesPrice();
        }

        BigDecimal discountPercentage = discounts.get(0).getDiscountPercentage();
        return applyDiscount(book.getSalesPrice(), discountPercentage);
    }

    private BigDecimal applyDiscount(BigDecimal price, BigDecimal percentage) {
        return price.multiply(
                BigDecimal.valueOf(100).subtract(percentage)
                        .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP)
        );
    }

    private List<Long> extractAuthorIds(Book book) {
        if (book.getAuthors() == null) return Collections.emptyList();
        return book.getAuthors().stream().map(Author::getId).toList();
    }
}
