package org.example.libraffstore.service;

import lombok.RequiredArgsConstructor;
import org.example.libraffstore.dto.EmployeeTransferContext;
import org.example.libraffstore.dto.request.EmployeeTransferRequest;
import org.example.libraffstore.dto.response.EmployeeTransferResponse;
import org.example.libraffstore.entity.Employee;
import org.example.libraffstore.entity.Position;
import org.example.libraffstore.entity.Store;
import org.example.libraffstore.enums.HistoryType;
import org.example.libraffstore.exception.BusinessException;
import org.example.libraffstore.exception.NotFoundException;
import org.example.libraffstore.mapper.EmployeeTransferMapper;
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
    private final EmployeeTransferMapper transferMapper;

    @Transactional
    public EmployeeTransferResponse transferEmployee(EmployeeTransferRequest request) {

        Employee employee = getEmployee(request.getEmployeeId());

        if (!employee.getIsActive())
            throw new BusinessException("Aktiv olmayan işçi transfer edilə bilməz.");

        Store toStore = getStore(request.getToStoreId());
        Position toPosition = getPosition(request.getToPositionId());

        employeeValidator.validateSalaryRange(request.getNewSalary(), toPosition);

        Store fromStore = employee.getStore();
        Position fromPosition = employee.getPosition();
        BigDecimal fromSalary = employee.getSalary();
        LocalDate transferDate = LocalDate.now();

        workHistoryService.closeCurrentHistory(employee.getId());

        employee.setStore(toStore);
        employee.setPosition(toPosition);
        employee.setSalary(request.getNewSalary());
        Employee saved = employeeRepository.save(employee);

        workHistoryService.saveHistory(saved, toStore, toPosition,
                request.getNewSalary(), transferDate, HistoryType.TRANSFERRED);

        EmployeeTransferContext context = EmployeeTransferContext.builder()
                .employee(saved)
                .fromStore(fromStore)
                .fromPosition(fromPosition)
                .fromSalary(fromSalary)
                .toStore(toStore)
                .toPosition(toPosition)
                .toSalary(request.getNewSalary())
                .transferDate(transferDate)
                .build();

        return transferMapper.toResponse(context);
    }

    private Employee getEmployee(Long id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("İşçi tapılmadı. ID: " + id));
    }

    private Store getStore(Long id) {
        return storeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Mağaza tapılmadı. ID: " + id));
    }

    private Position getPosition(Long id) {
        return positionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Vəzifə tapılmadı. ID: " + id));
    }
}