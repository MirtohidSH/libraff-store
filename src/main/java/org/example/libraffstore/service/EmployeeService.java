package org.example.libraffstore.service;

import lombok.RequiredArgsConstructor;
import org.example.libraffstore.dto.request.EmployeeRequest;
import org.example.libraffstore.dto.response.EmployeeResponse;
import org.example.libraffstore.entity.Employee;
import org.example.libraffstore.entity.Position;
import org.example.libraffstore.entity.Store;
import org.example.libraffstore.enums.HistoryType;
import org.example.libraffstore.exception.AlreadyExistsException;
import org.example.libraffstore.exception.BusinessException;
import org.example.libraffstore.exception.NotFoundException;
import org.example.libraffstore.mapper.EmployeeMapper;
import org.example.libraffstore.repository.EmployeeRepository;
import org.example.libraffstore.repository.PositionRepository;
import org.example.libraffstore.repository.StoreRepository;
import org.example.libraffstore.validator.EmployeeValidator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final StoreRepository storeRepository;
    private final PositionRepository positionRepository;
    private final EmployeeWorkHistoryService workHistoryService;
    private final EmployeeValidator employeeValidator;
    private final EmployeeMapper employeeMapper;

    @Transactional
    public EmployeeResponse addEmployee(EmployeeRequest request) {

        Store store = storeRepository.findById(request.getStoreId())
                .orElseThrow(() -> new NotFoundException("Mağaza tapılmadı. ID: " + request.getStoreId()));

        Position position = positionRepository.findById(request.getPositionId())
                .orElseThrow(() -> new NotFoundException("Vəzifə tapılmadı. ID: " + request.getPositionId()));

        employeeValidator.validateSalaryRange(request.getSalary(), position);

        return employeeRepository.findByFIN(request.getFIN())
                .map(existing -> {
                    if (existing.getIsActive())
                        throw new AlreadyExistsException("Bu FIN ilə aktiv işçi artıq mövcuddur: " + request.getFIN());
                    return rehire(existing, request, store, position);
                })
                .orElseGet(() -> {
                    employeeValidator.validateNewEmployee(request.getFIN(), request.getEmail(), request.getPhone());

                    Employee employee = buildEmployee(request, store, position);
                    Employee saved = employeeRepository.save(employee);

                    workHistoryService.saveHistory(saved, store, position, saved.getSalary(), saved.getDateEmployed(), HistoryType.HIRED);

                    return employeeMapper.toResponse(saved);
                });
    }

    public List<EmployeeResponse> findAll() {
        return employeeRepository.findAll()
                .stream()
                .map(employeeMapper::toResponse)
                .toList();
    }

    public EmployeeResponse findById(Long employeeId) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new NotFoundException("İşçi tapılmadı. ID: " + employeeId));
        return employeeMapper.toResponse(employee);
    }

    public List<EmployeeResponse> findAllActive() {
        return employeeRepository.findAllByIsActiveTrue()
                .stream()
                .map(employeeMapper::toResponse)
                .toList();
    }

    @Transactional
    public EmployeeResponse updateEmployee(Long employeeId, EmployeeRequest request) {

        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new NotFoundException("İşçi tapılmadı. ID: " + employeeId));

        employeeValidator.validateUpdateEmployee(
                employeeId, request.getFIN(), request.getEmail(), request.getPhone());

        employee.setFIN(request.getFIN());
        employee.setFirstName(request.getFirstName());
        employee.setLastName(request.getLastName());
        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            employee.setPassword(request.getPassword());
        }
        employee.setEmail(request.getEmail());
        employee.setPhone(request.getPhone());

        return employeeMapper.toResponse(employeeRepository.save(employee));
    }

    @Transactional
    public void deleteEmployee(Long employeeId) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new NotFoundException("İşçi tapılmadı. ID: " + employeeId));

        if (!employee.getIsActive())
            throw new BusinessException("İşçi artıq deaktivdir.");

        workHistoryService.closeCurrentHistory(employeeId);

        employee.setIsActive(false);
        employee.setDateUnemployed(LocalDate.now());
        employeeRepository.save(employee);

        workHistoryService.saveHistory(employee, employee.getStore(), employee.getPosition(),
                employee.getSalary(), LocalDate.now(), HistoryType.RESIGNED);
    }

    private EmployeeResponse rehire(Employee employee, EmployeeRequest request, Store store, Position position) {
        employee.setFirstName(request.getFirstName());
        employee.setLastName(request.getLastName());
        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            employee.setPassword(request.getPassword());
        }
        employee.setEmail(request.getEmail());
        employee.setPhone(request.getPhone());
        employee.setSalary(request.getSalary());
        employee.setStore(store);
        employee.setPosition(position);
        employee.setIsActive(true);
        employee.setDateEmployed(request.getDateEmployed());
        employee.setDateUnemployed(null);

        Employee saved = employeeRepository.save(employee);

        workHistoryService.saveHistory(saved, store, position, saved.getSalary(), saved.getDateEmployed(), HistoryType.REHIRED);

        return employeeMapper.toResponse(saved);
    }

    private static Employee buildEmployee(EmployeeRequest request, Store store, Position position) {
        Employee employee = new Employee();
        employee.setFIN(request.getFIN());
        employee.setFirstName(request.getFirstName());
        employee.setLastName(request.getLastName());
        employee.setPassword(request.getPassword());
        employee.setIsActive(true);
        employee.setEmail(request.getEmail());
        employee.setPhone(request.getPhone());
        employee.setSalary(request.getSalary());
        employee.setDateEmployed(request.getDateEmployed());
        employee.setDateUnemployed(null);
        employee.setStore(store);
        employee.setPosition(position);
        return employee;
    }
}