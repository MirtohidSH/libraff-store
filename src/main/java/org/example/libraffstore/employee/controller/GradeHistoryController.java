package org.example.libraffstore.employee.controller;

import lombok.RequiredArgsConstructor;
import org.example.libraffstore.employee.dto.GradeHistoryResponse;
import org.example.libraffstore.employee.service.GradeHistoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(path = "/grade-history")
@RequiredArgsConstructor
public class GradeHistoryController {

    private final GradeHistoryService gradeHistoryService;

    @GetMapping
    public ResponseEntity<List<GradeHistoryResponse>> getAll() {
        return ResponseEntity.ok(gradeHistoryService.findAll());
    }

}

