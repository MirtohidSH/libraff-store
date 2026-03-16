package org.example.libraffstore.dto.request;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class DiscountRequest {

    @NotBlank(message = "Endirim adı boş ola bilməz")
    private String name;

    @NotNull(message = "Endirim faizi boş ola bilməz")
    @DecimalMin(value = "5.0", message = "Endirim minimum 5% olmalıdır")
    @DecimalMax(value = "40.0", message = "Endirim maksimum 40% ola bilər")
    private BigDecimal discountPercentage;

    @NotNull(message = "Başlama tarixi boş ola bilməz")
    private LocalDate startDate;

    @NotNull(message = "Bitmə tarixi boş ola bilməz")
    private LocalDate endDate;

    private Boolean isActive = true;
    private Long bookId;
    private Long authorId;
    private Long genreId;
    private Long storeId;

}
