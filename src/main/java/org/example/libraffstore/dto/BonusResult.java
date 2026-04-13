package org.example.libraffstore.dto;

import java.math.BigDecimal;

public record BonusResult(BigDecimal achievedSales, BigDecimal bonusAmount) {

    public static BonusResult zero(BigDecimal sales) {
        return new BonusResult(sales, BigDecimal.ZERO);
    }
}