package org.example.libraffstore.dto;

import lombok.Builder;
import lombok.Data;
import org.example.libraffstore.entity.Employee;
import org.example.libraffstore.entity.Position;
import org.example.libraffstore.entity.Store;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
public class EmployeeTransferContext {
    private Employee employee;
    private Store fromStore;
    private Position fromPosition;
    private BigDecimal fromSalary;
    private Store toStore;
    private Position toPosition;
    private BigDecimal toSalary;
    private LocalDate transferDate;
}