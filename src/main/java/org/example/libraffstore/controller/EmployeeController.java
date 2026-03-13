package org.example.libraffstore.controller;

import lombok.RequiredArgsConstructor;
import org.example.libraffstore.dto.request.EmployeeRequest;
import org.example.libraffstore.dto.response.EmployeeResponse;
import org.example.libraffstore.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path="/employees")
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

    @PostMapping(path = "/add-employee")
    public ResponseEntity<EmployeeResponse> addEmployee(@RequestBody EmployeeRequest employeeRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(employeeService.addEmployee(employeeRequest));
    }

    @GetMapping("/active")
    public ResponseEntity<List<EmployeeResponse>> getActive() {
        return ResponseEntity.ok(employeeService.findAllActive());
    }


    @PatchMapping("/{id}/delete-employee")
    public ResponseEntity<Void> deleteEmployee(@PathVariable Long id) {
        employeeService.deleteEmployee(id);
        return ResponseEntity.noContent().build();
    }
}