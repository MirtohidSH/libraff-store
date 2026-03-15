package org.example.libraffstore.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.libraffstore.dto.request.EmployeeRequest;
import org.example.libraffstore.dto.request.EmployeeTransferRequest;
import org.example.libraffstore.dto.response.EmployeeResponse;
import org.example.libraffstore.service.EmployeeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/employees")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;

    @GetMapping
    public ResponseEntity<List<EmployeeResponse>> getAll() {
        return ResponseEntity.ok(employeeService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmployeeResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(employeeService.findById(id));
    }

    @GetMapping("/active")
    public ResponseEntity<List<EmployeeResponse>> getActive() {
        return ResponseEntity.ok(employeeService.findAllActive());
    }

    @PostMapping
    public ResponseEntity<EmployeeResponse> add(@Valid @RequestBody EmployeeRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(employeeService.addEmployee(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EmployeeResponse> update(@PathVariable Long id,
                                                   @Valid @RequestBody EmployeeRequest request) {
        return ResponseEntity.ok(employeeService.updateEmployee(id, request));
    }

    @PostMapping("/transfer")
    public ResponseEntity<EmployeeResponse> transfer(@Valid @RequestBody EmployeeTransferRequest request) {
        return ResponseEntity.ok(employeeService.transferEmployee(request));
    }

    @PatchMapping("/{id}/delete-employee")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        employeeService.deleteEmployee(id);
        return ResponseEntity.noContent().build();
    }
}