package org.example.libraffstore.service.helper;

import org.example.libraffstore.entity.Author;
import org.example.libraffstore.entity.Book;
import org.example.libraffstore.entity.Discount;
import org.example.libraffstore.entity.Genre;
import org.example.libraffstore.repository.DiscountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DiscountPriceCalculatorTest {

    @Mock
    private DiscountRepository discountRepository;

    @InjectMocks
    private DiscountPriceCalculator discountPriceCalculator;

    private Book book;

    @BeforeEach
    void setUp() {
        Author author = new Author();
        author.setId(1L);

        Genre genre = new Genre();
        genre.setId(2L);

        book = new Book();
        book.setId(10L);
        book.setSalesPrice(new BigDecimal("100.00"));
        book.setAuthors(Set.of(author));
        book.setGenre(genre);
    }

    @Test
    void calculate_whenNoDiscounts_shouldReturnOriginalPrice() {
        when(discountRepository.findApplicableDiscounts(any(), any(), any(), any(), any()))
                .thenReturn(Collections.emptyList());

        BigDecimal result = discountPriceCalculator.calculate(book, 1L);

        assertThat(result).isEqualByComparingTo("100.00");
    }

    @Test
    void calculate_whenDiscountExists_shouldReturnDiscountedPrice() {
        Discount discount = new Discount();
        discount.setDiscountPercentage(new BigDecimal("20"));

        when(discountRepository.findApplicableDiscounts(any(), any(), any(), any(), any()))
                .thenReturn(List.of(discount));

        BigDecimal result = discountPriceCalculator.calculate(book, 1L);

        assertThat(result).isEqualByComparingTo("80.00");
    }

    @Test
    void calculate_whenMultipleDiscounts_shouldApplyHighestFirst() {
        // 30% endirim → 100.00 * 0.70 = 70.00
        Discount highDiscount = new Discount();
        highDiscount.setDiscountPercentage(new BigDecimal("30"));

        Discount lowDiscount = new Discount();
        lowDiscount.setDiscountPercentage(new BigDecimal("10"));

        when(discountRepository.findApplicableDiscounts(any(), any(), any(), any(), any()))
                .thenReturn(List.of(highDiscount, lowDiscount));

        BigDecimal result = discountPriceCalculator.calculate(book, 1L);

        assertThat(result).isEqualByComparingTo("70.00");
    }

    @Test
    void calculate_whenBookHasNoAuthors_shouldStillWork() {
        book.setAuthors(null);

        when(discountRepository.findApplicableDiscounts(any(), any(), any(), any(), any()))
                .thenReturn(Collections.emptyList());

        BigDecimal result = discountPriceCalculator.calculate(book, 1L);

        assertThat(result).isEqualByComparingTo("100.00");
    }

    @Test
    void calculate_whenBookHasNoGenre_shouldStillWork() {
        book.setGenre(null);

        when(discountRepository.findApplicableDiscounts(any(), any(), any(), any(), any()))
                .thenReturn(Collections.emptyList());

        BigDecimal result = discountPriceCalculator.calculate(book, 1L);

        assertThat(result).isEqualByComparingTo("100.00");
    }

    @Test
    void calculate_whenDiscountIs5Percent_shouldReturnCorrectPrice() {
        Discount discount = new Discount();
        discount.setDiscountPercentage(new BigDecimal("5"));

        when(discountRepository.findApplicableDiscounts(any(), any(), any(), any(), any()))
                .thenReturn(List.of(discount));

        BigDecimal result = discountPriceCalculator.calculate(book, 1L);

        assertThat(result).isEqualByComparingTo("95.00");
    }

    @Test
    void calculate_whenDiscountIs40Percent_shouldReturnCorrectPrice() {
        Discount discount = new Discount();
        discount.setDiscountPercentage(new BigDecimal("40"));

        when(discountRepository.findApplicableDiscounts(any(), any(), any(), any(), any()))
                .thenReturn(List.of(discount));

        BigDecimal result = discountPriceCalculator.calculate(book, 1L);

        assertThat(result).isEqualByComparingTo("60.00");
    }
}