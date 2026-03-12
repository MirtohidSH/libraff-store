package org.example.libraffstore.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.libraffstore.enums.PeriodType;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GradeHistoryResponse {

    private Long id;

    private String employeeFullName;

    private String gradeName;

    private BigDecimal achievedSales;

    private BigDecimal appliedThreshold;

    private BigDecimal calculatedGradeAmount;

    private String periodType;

    private String positionType;

    private String storeName;

    private LocalDate periodStart;

    private LocalDate periodEnd;

}
