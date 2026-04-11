package org.example.libraffstore.service;

import lombok.RequiredArgsConstructor;
import org.example.libraffstore.entity.Employee;
import org.example.libraffstore.entity.EmployeeWorkHistory;
import org.example.libraffstore.entity.Position;
import org.example.libraffstore.entity.Store;
import org.example.libraffstore.enums.HistoryType;
import org.example.libraffstore.repository.EmployeeWorkHistoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class EmployeeWorkHistoryService {

    private final EmployeeWorkHistoryRepository workHistoryRepository;

    @Transactional
    public void saveHistory(Employee employee, Store store, Position position,
                            BigDecimal salary, LocalDate startDate, HistoryType historyType) {
        workHistoryRepository.save(buildHistory(employee, store, position, salary, startDate, historyType));
    }

    @Transactional
    public void closeCurrentHistory(Long employeeId) {
        workHistoryRepository.findByEmployeeIdAndEndDateIsNull(employeeId)
                .ifPresent(history -> {
                    history.setEndDate(LocalDate.now());
                    workHistoryRepository.save(history);
                });
    }

    private EmployeeWorkHistory buildHistory(Employee employee, Store store, Position position,
                                             BigDecimal salary, LocalDate startDate, HistoryType historyType) {
        EmployeeWorkHistory history = new EmployeeWorkHistory();
        history.setEmployee(employee);
        history.setStore(store);
        history.setPosition(position);
        history.setSalary(salary);
        history.setIsActive(employee.getIsActive());
        history.setStartDate(startDate);
        history.setEndDate(null);
        history.setHistoryType(historyType);
        return history;
    }
}