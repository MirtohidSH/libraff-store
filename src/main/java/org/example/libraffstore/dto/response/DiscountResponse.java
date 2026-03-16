package org.example.libraffstore.dto.response;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
public class DiscountResponse {

    private Long id;
    private String name;
    private BigDecimal discountPercentage;
    private LocalDate startDate;
    private LocalDate endDate;
    private Boolean isActive;

    private String bookName;
    private String authorName;
    private String genreName;
    private String storeName;
}