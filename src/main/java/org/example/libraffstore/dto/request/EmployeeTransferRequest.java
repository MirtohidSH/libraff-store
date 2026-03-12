package org.example.libraffstore.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class EmployeeTransferRequest {

    @NotNull(message = "İşçi ID boş ola bilməz")
    private Long employeeId;

    @NotNull(message = "Yeni mağaza ID boş ola bilməz")
    private Long toStoreId;

    @NotNull(message = "Yeni vəzifə ID boş ola bilməz")
    private Long toPositionId;

    @NotNull(message = "Yeni maaş boş ola bilməz")
    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal newSalary;
}