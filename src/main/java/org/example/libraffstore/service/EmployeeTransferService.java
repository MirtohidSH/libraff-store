package org.example.libraffstore.service;

import lombok.RequiredArgsConstructor;
import org.example.libraffstore.dto.request.EmployeeTransferRequest;
import org.example.libraffstore.dto.response.EmployeeTransferResponse;
import org.example.libraffstore.entity.Employee;
import org.example.libraffstore.entity.Position;
import org.example.libraffstore.entity.Store;
import org.example.libraffstore.enums.HistoryType;
import org.example.libraffstore.exception.BusinessException;
import org.example.libraffstore.exception.NotFoundException;
import org.example.libraffstore.repository.EmployeeRepository;
import org.example.libraffstore.repository.PositionRepository;
import org.example.libraffstore.repository.StoreRepository;
import org.example.libraffstore.validator.EmployeeValidator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class EmployeeTransferService {

    private final EmployeeRepository employeeRepository;
    private final StoreRepository storeRepository;
    private final PositionRepository positionRepository;
    private final EmployeeWorkHistoryService workHistoryService;
    private final EmployeeValidator employeeValidator;

    @Transactional
    public EmployeeTransferResponse transferEmployee(EmployeeTransferRequest request) {

        Employee employee = employeeRepository.findById(request.getEmployeeId())
                .orElseThrow(() -> new NotFoundException("İşçi tapılmadı. ID: " + request.getEmployeeId()));

        if (!employee.getIsActive())
            throw new BusinessException("Aktiv olmayan işçi transfer edilə bilməz.");

        Store toStore = storeRepository.findById(request.getToStoreId())
                .orElseThrow(() -> new NotFoundException("Mağaza tapılmadı. ID: " + request.getToStoreId()));

        Position toPosition = positionRepository.findById(request.getToPositionId())
                .orElseThrow(() -> new NotFoundException("Vəzifə tapılmadı. ID: " + request.getToPositionId()));

        employeeValidator.validateSalaryRange(request.getNewSalary(), toPosition);

        Store fromStore = employee.getStore();
        Position fromPosition = employee.getPosition();
        BigDecimal fromSalary = employee.getSalary();

        workHistoryService.closeCurrentHistory(employee.getId());

        employee.setStore(toStore);
        employee.setPosition(toPosition);
        employee.setSalary(request.getNewSalary());
        Employee saved = employeeRepository.save(employee);

        workHistoryService.saveHistory(saved, toStore, toPosition,
                request.getNewSalary(), LocalDate.now(), HistoryType.TRANSFERRED);

        return toResponse(saved, fromStore, fromPosition, fromSalary, toStore, toPosition, request.getNewSalary());
    }

    private EmployeeTransferResponse toResponse(Employee employee, Store fromStore, Position fromPosition, BigDecimal fromSalary,
                                                Store toStore, Position toPosition, BigDecimal toSalary) {
        EmployeeTransferResponse response = new EmployeeTransferResponse();
        response.setEmployeeId(employee.getId());
        response.setFirstName(employee.getFirstName());
        response.setLastName(employee.getLastName());
        response.setFromStoreName(fromStore.getName());
        response.setToStoreName(toStore.getName());
        response.setFromPositionType(fromPosition.getPositionType());
        response.setToPositionType(toPosition.getPositionType());
        response.setFromSalary(fromSalary);
        response.setToSalary(toSalary);
        response.setTransferDate(LocalDate.now());
        return response;
    }
}