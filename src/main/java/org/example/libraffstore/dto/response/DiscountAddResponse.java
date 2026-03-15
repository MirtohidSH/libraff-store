package org.example.libraffstore.dto.response;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DiscountAddResponse {

    Long id;
    private String discountName;
    LocalDate startDate;
    LocalDate endDate;
}