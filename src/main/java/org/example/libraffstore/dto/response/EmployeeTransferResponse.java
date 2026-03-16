package org.example.libraffstore.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.libraffstore.enums.PositionType;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeTransferResponse {

    private Long employeeId;
    private String firstName;
    private String lastName;

    private String fromStoreName;
    private String toStoreName;

    private PositionType fromPositionType;
    private PositionType toPositionType;

    private BigDecimal fromSalary;
    private BigDecimal toSalary;

    private LocalDate transferDate;
}
