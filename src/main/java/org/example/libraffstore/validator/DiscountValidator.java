package org.example.libraffstore.validator;

import org.example.libraffstore.dto.request.DiscountRequest;
import org.example.libraffstore.exception.BusinessException;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Objects;
import java.util.stream.Stream;

@Component
public class DiscountValidator {

    public void validateTargets(DiscountRequest request) {
        long filledCount = Stream.of(
                request.getBookId(),
                request.getAuthorId(),
                request.getGenreId(),
                request.getStoreId()
        ).filter(Objects::nonNull).count();

        if (filledCount == 0)
            throw new BusinessException("Endirim ən azı bir hədəfə tətbiq edilməlidir.");
        if (filledCount > 1)
            throw new BusinessException("Endirim yalnız bir hədəfə tətbiq edilə bilər.");
    }

    public void validateDateRange(LocalDate startDate, LocalDate endDate) {
        if (startDate.isAfter(endDate))
            throw new BusinessException("Başlama tarixi bitmə tarixindən sonra ola bilməz.");
    }
}