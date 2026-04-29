package org.example.libraffstore.employee.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.libraffstore.employee.dto.EmployeeTransferRequest;
import org.example.libraffstore.employee.dto.EmployeeTransferResponse;
import org.example.libraffstore.employee.service.EmployeeTransferService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/emloyee-transfer")
@RequiredArgsConstructor
public class EmployeeTransferController {

    private final EmployeeTransferService employeeTransferService;

    @PostMapping
    public ResponseEntity<EmployeeTransferResponse> transfer(@Valid @RequestBody EmployeeTransferRequest request) {
        return ResponseEntity.ok(employeeTransferService.transferEmployee(request));
    }
}

