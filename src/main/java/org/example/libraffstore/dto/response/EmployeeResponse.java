package org.example.libraffstore.dto.response;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.libraffstore.enums.PositionType;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
public class EmployeeResponse {

    private Long id;
    private String FIN;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private Boolean isActive;
    private BigDecimal salary;
    private LocalDate dateEmployed;
    private LocalDate dateUnemployed;
    private String storeName;
    private String storeAddress;
    private PositionType positionType;
}