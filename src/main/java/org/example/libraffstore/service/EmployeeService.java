package org.example.libraffstore.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.libraffstore.dto.request.EmployeeRequest;
import org.example.libraffstore.entity.Employee;
import org.example.libraffstore.dto.response.EmployeeResponse;
import org.example.libraffstore.entity.Position;
import org.example.libraffstore.entity.Store;
import org.example.libraffstore.exception.AlreadyExistsException;
import org.example.libraffstore.exception.BusinessException;
import org.example.libraffstore.exception.NotFoundException;
import org.example.libraffstore.repository.EmployeeRepository;
import org.example.libraffstore.repository.PositionRepository;
import org.example.libraffstore.repository.StoreRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final StoreRepository storeRepository;
    private final PositionRepository positionRepository;

    //CREATE
    @Transactional
    public EmployeeResponse addEmployee(EmployeeRequest employeeRequest) {

        if(employeeRepository.existsByFIN(employeeRequest.getFIN())) {
            throw new AlreadyExistsException("Bu FIN ilə işçi artıq mövcuddur: " + employeeRequest.getFIN());
        }
        if(employeeRepository.existsByEmail(employeeRequest.getEmail())){
            throw new AlreadyExistsException("Bu email artıq istifadə olunur: " + employeeRequest.getEmail());
        }
        if(employeeRepository.existsByPhone(employeeRequest.getPhone())){
            throw new AlreadyExistsException("Bu telefon nömrəsi artıq istifadə olunur: " + employeeRequest.getPhone());
        }
        Store store = storeRepository.findById(employeeRequest.getStoreId())
                .orElseThrow(() -> new NotFoundException("Mağaza tapılmadı. ID: " + employeeRequest.getStoreId()));

        Position position = positionRepository.findById(employeeRequest.getPositionId())
                .orElseThrow(() -> new NotFoundException("Vəzifə tapılmadı. ID: " + employeeRequest.getPositionId()));

        validateSalaryRange(employeeRequest.getSalary(), position);

        Employee employee = new Employee();
        employee.setFIN(employeeRequest.getFIN());
        employee.setFirstName(employeeRequest.getFirstName());
        employee.setLastName(employeeRequest.getLastName());
        employee.setPassword(employeeRequest.getPassword());
        employee.setIsActive(employeeRequest.getIsActive() != null ? employeeRequest.getIsActive() : false);
        employee.setEmail(employeeRequest.getEmail());
        employee.setPhone(employeeRequest.getPhone());
        employee.setSalary(employeeRequest.getSalary());
        employee.setDateEmployed(employeeRequest.getDateEmployed());
        employee.setDateUnemployed(employeeRequest.getDateUnemployed());
        employee.setStore(store);
        employee.setPosition(position);

        Employee saved = employeeRepository.save(employee);
        return toResponse(saved);
    }

    //READ
    public List<EmployeeResponse> findAll() {
        return employeeRepository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public EmployeeResponse findById(Long employeeId) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new NotFoundException("Işçi tapılmadı. ID: " + employeeId));
        return toResponse(employee);
    }

    public List<EmployeeResponse> findAllActive() {
        return employeeRepository.findAllByIsActiveTrue()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }


    //DELETE
    @Transactional
    public void deleteEmployee(Long employeeId) {
       Employee employee = employeeRepository.findById(employeeId)
               .orElseThrow(() -> new NotFoundException("Işçi tapılmadı. ID: " + employeeId));
       employee.setIsActive(false);
       employeeRepository.save(employee);
    }

    private void validateSalaryRange(BigDecimal salary, Position position){
        if(position.getMinSalary() != null && salary.compareTo(BigDecimal.valueOf(position.getMinSalary())) < 0){
            throw new BusinessException(String.format(
                    "%s vəzifəsi üçün minimum maaş %d AZN-dir. Daxil edilən: %.2f AZN",
                    position.getPositionType().name(), position.getMinSalary(), salary
            ));
        }
        if (position.getMaxSalary() != null && salary.compareTo(BigDecimal.valueOf(position.getMaxSalary())) > 0) {
            throw new BusinessException(String.format(
                    "%s vəzifəsi üçün maksimum maaş %d AZN-dir. Daxil edilən: %.2f AZN",
                    position.getPositionType().name(), position.getMaxSalary(), salary
            ));
        }
    }

    //MAPPER
    private EmployeeResponse toResponse(Employee employee) {
        EmployeeResponse response = new EmployeeResponse();
        response.setId(employee.getId());
        response.setFIN(employee.getFIN());
        response.setFirstName(employee.getFirstName());
        response.setLastName(employee.getLastName());
        response.setEmail(employee.getEmail());
        response.setPhone(employee.getPhone());
        response.setIsActive(employee.getIsActive());
        response.setSalary(employee.getSalary());
        response.setDateEmployed(employee.getDateEmployed());
        response.setDateUnemployed(employee.getDateUnemployed());

        if (employee.getStore() != null) {
            response.setStoreName(employee.getStore().getName());
            response.setStoreAddress(employee.getStore().getAddress());
        }
        if (employee.getPosition() != null) {
            response.setPositionType(employee.getPosition().getPositionType());
        }
        return response;
    }
}
