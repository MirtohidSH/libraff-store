package org.example.libraffstore.service;

import lombok.RequiredArgsConstructor;
import org.example.libraffstore.dto.request.EmployeeAddRequest;
import org.example.libraffstore.dto.request.EmployeeRequest;
import org.example.libraffstore.dto.response.EmployeeResponse;
import org.example.libraffstore.entity.Employee;
import org.example.libraffstore.entity.Position;
import org.example.libraffstore.entity.Store;
import org.example.libraffstore.enums.HistoryType;
import org.example.libraffstore.exception.AlreadyExistsException;
import org.example.libraffstore.exception.NotFoundException;
import org.example.libraffstore.mapper.EmployeeMapper;
import org.example.libraffstore.repository.EmployeeRepository;
import org.example.libraffstore.repository.PositionRepository;
import org.example.libraffstore.repository.StoreRepository;
import org.example.libraffstore.validator.EmployeeValidator;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final StoreRepository storeRepository;
    private final PositionRepository positionRepository;
    private final EmployeeWorkHistoryService workHistoryService;
    private final EmployeeValidator employeeValidator;
    private final EmployeeMapper employeeMapper;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public EmployeeResponse addEmployee(EmployeeRequest request) {
        Store store = getStore(request.getStoreId());
        Position position = getPosition(request.getPositionId());

        employeeValidator.validateSalaryRange(request.getSalary(), position);

        Optional<Employee> existingEmployeeOpt = employeeRepository.findByFIN(request.getFIN());

        if (existingEmployeeOpt.isPresent()) {
            return processRehire(existingEmployeeOpt.get(), request, store, position);
        } else {
            return processNewHire(request, store, position);
        }
    }

    public Page<EmployeeResponse> findAll(Pageable pageable) {
        return employeeRepository.findAll(pageable)
                .map(employeeMapper::toResponse);
    }

    public Page<EmployeeResponse> findAllActive(Pageable pageable) {
        return employeeRepository.findAllByIsActiveTrue(pageable)
                .map(employeeMapper::toResponse);
    }

    public EmployeeResponse findById(Long employeeId) {
        return employeeMapper.toResponse(getEmployee(employeeId));
    }

    @Transactional
    public EmployeeResponse updateEmployee(Long employeeId, EmployeeRequest request) {
        Employee employee = getEmployee(employeeId);

        employeeValidator.validateUpdateEmployee(employeeId, request.getFIN(), request.getEmail(), request.getPhone());
        employeeMapper.updateEmployeeFromRequest(request, employee);

        updatePasswordIfProvided(employee, request.getPassword());

        return employeeMapper.toResponse(employeeRepository.save(employee));
    }

    @Transactional
    public void terminateEmployee(Long employeeId) {
        Employee employee = getEmployee(employeeId);
        LocalDate terminationDate = LocalDate.now();

        workHistoryService.closeCurrentHistory(employeeId);

        employee.terminate(terminationDate);
        employeeRepository.save(employee);

        workHistoryService.saveHistory(
                employee, employee.getStore(), employee.getPosition(),
                employee.getSalary(), terminationDate, HistoryType.RESIGNED
        );
    }

    private EmployeeResponse processNewHire(EmployeeRequest request, Store store, Position position) {
        employeeValidator.validateNewEmployee(request.getFIN(), request.getEmail(), request.getPhone());

        Employee employee = employeeMapper.toEntity(request);
        employee.setStore(store);
        employee.setPosition(position);
        updatePasswordIfProvided(employee, request.getPassword());

        Employee saved = employeeRepository.save(employee);

        workHistoryService.saveHistory(
                saved, store, position, saved.getSalary(),
                saved.getDateEmployed(), HistoryType.HIRED
        );

        return employeeMapper.toResponse(saved);
    }

    private EmployeeResponse processRehire(Employee employee, EmployeeRequest request, Store store, Position position) {

        if (employee.getIsActive())
            throw new AlreadyExistsException("Bu FIN ilə aktiv işçi artıq mövcuddur: " + employee.getFIN());

        employee.rehire(store, position, request.getDateEmployed());

        employeeMapper.updateEmployeeFromRequest(request, employee);
        updatePasswordIfProvided(employee, request.getPassword());

        Employee saved = employeeRepository.save(employee);

        workHistoryService.saveHistory(
                saved, store, position, saved.getSalary(),
                saved.getDateEmployed(), HistoryType.REHIRED
        );

        return employeeMapper.toResponse(saved);
    }

    private void updatePasswordIfProvided(Employee employee, String rawPassword) {
        if (rawPassword != null && !rawPassword.isBlank()) {
            employee.setPassword(passwordEncoder.encode(rawPassword));
        }
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