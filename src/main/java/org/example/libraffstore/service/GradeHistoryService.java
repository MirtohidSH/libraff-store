package org.example.libraffstore.service;

import lombok.RequiredArgsConstructor;
import org.example.libraffstore.dto.response.GradeHistoryResponse;
import org.example.libraffstore.entity.Employee;
import org.example.libraffstore.entity.GradeHistory;
import org.example.libraffstore.entity.GradeStructure;
import org.example.libraffstore.repository.GradeHistoryRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GradeHistoryService {

    private final GradeHistoryRepository gradeHistoryRepository;

    private final ModelMapper modelMapper;

    public List<GradeHistoryResponse> findAll() {
        return gradeHistoryRepository.findAllWithDetails()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    private GradeHistoryResponse toResponse(GradeHistory gh) {
        return modelMapper.map(gh, GradeHistoryResponse.class);
    }
}