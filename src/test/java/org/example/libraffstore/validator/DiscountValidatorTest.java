package org.example.libraffstore.validator;

import org.example.libraffstore.dto.request.DiscountRequest;
import org.example.libraffstore.exception.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.assertThatCode;

class DiscountValidatorTest {

    private DiscountValidator discountValidator;

    @BeforeEach
    void setUp() {
        discountValidator = new DiscountValidator();
    }

    @Test
    void validateTargets_whenBookIdProvided_shouldNotThrow() {
        DiscountRequest request = new DiscountRequest();
        request.setBookId(1L);

        assertThatCode(() -> discountValidator.validateTargets(request))
                .doesNotThrowAnyException();
    }

    @Test
    void validateTargets_whenAuthorIdProvided_shouldNotThrow() {
        DiscountRequest request = new DiscountRequest();
        request.setAuthorId(1L);

        assertThatCode(() -> discountValidator.validateTargets(request))
                .doesNotThrowAnyException();
    }

    @Test
    void validateTargets_whenGenreIdProvided_shouldNotThrow() {
        DiscountRequest request = new DiscountRequest();
        request.setGenreId(1L);

        assertThatCode(() -> discountValidator.validateTargets(request))
                .doesNotThrowAnyException();
    }

    @Test
    void validateTargets_whenStoreIdProvided_shouldNotThrow() {
        DiscountRequest request = new DiscountRequest();
        request.setStoreId(1L);

        assertThatCode(() -> discountValidator.validateTargets(request))
                .doesNotThrowAnyException();
    }

    @Test
    void validateTargets_whenNoTargetProvided_shouldThrow() {
        DiscountRequest request = new DiscountRequest();

        assertThatThrownBy(() -> discountValidator.validateTargets(request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("ən azı bir hədəfə");
    }

    @Test
    void validateTargets_whenMultipleTargetsProvided_shouldThrow() {
        DiscountRequest request = new DiscountRequest();
        request.setBookId(1L);
        request.setAuthorId(2L);

        assertThatThrownBy(() -> discountValidator.validateTargets(request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("yalnız bir hədəfə");
    }

    @Test
    void validateTargets_whenAllTargetsProvided_shouldThrow() {
        DiscountRequest request = new DiscountRequest();
        request.setBookId(1L);
        request.setAuthorId(2L);
        request.setGenreId(3L);
        request.setStoreId(4L);

        assertThatThrownBy(() -> discountValidator.validateTargets(request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("yalnız bir hədəfə");
    }

    @Test
    void validateDateRange_whenStartBeforeEnd_shouldNotThrow() {
        LocalDate start = LocalDate.of(2024, 1, 1);
        LocalDate end   = LocalDate.of(2024, 12, 31);

        assertThatCode(() -> discountValidator.validateDateRange(start, end))
                .doesNotThrowAnyException();
    }

    @Test
    void validateDateRange_whenStartEqualsEnd_shouldNotThrow() {
        LocalDate date = LocalDate.of(2024, 6, 15);

        assertThatCode(() -> discountValidator.validateDateRange(date, date))
                .doesNotThrowAnyException();
    }

    @Test
    void validateDateRange_whenStartAfterEnd_shouldThrow() {
        LocalDate start = LocalDate.of(2024, 12, 31);
        LocalDate end   = LocalDate.of(2024, 1, 1);

        assertThatThrownBy(() -> discountValidator.validateDateRange(start, end))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Başlama tarixi");
    }
}