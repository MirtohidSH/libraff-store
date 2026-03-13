package org.example.libraffstore.controller;

import lombok.RequiredArgsConstructor;
import org.example.libraffstore.service.PayrollService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/payroll")
@RequiredArgsConstructor
public class PayrollController {

    private final PayrollService payrollService;

    @PostMapping
    public ResponseEntity<?> payMonthlySalary() {
        return ResponseEntity.ok().build();
    }
}
