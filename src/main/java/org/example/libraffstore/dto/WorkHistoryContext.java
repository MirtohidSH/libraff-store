package org.example.libraffstore.dto;

import lombok.Builder;
import lombok.Data;
import org.example.libraffstore.entity.Employee;
import org.example.libraffstore.entity.Position;
import org.example.libraffstore.entity.Store;
import org.example.libraffstore.enums.HistoryType;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
public class WorkHistoryContext {
    private Employee employee;
    private Store store;
    private Position position;
    private BigDecimal salary;
    private LocalDate startDate;
    private HistoryType historyType;
}
