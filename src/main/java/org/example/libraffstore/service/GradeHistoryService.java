package org.example.libraffstore.service;

import lombok.RequiredArgsConstructor;
import org.example.libraffstore.dto.response.GradeHistoryResponse;
import org.example.libraffstore.entity.GradeHistory;
import org.example.libraffstore.mapper.GradeHistoryMapper;
import org.example.libraffstore.repository.GradeHistoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GradeHistoryService {

    private final GradeHistoryRepository gradeHistoryRepository;
    private final GradeHistoryMapper gradeHistoryMapper;

    @Transactional
    public List<GradeHistoryResponse> findAll() {
        return gradeHistoryRepository.findAll()
                .stream()
                .map(gradeHistoryMapper::toResponse)
                .toList();
    }

    private GradeHistoryResponse toResponse(GradeHistory gh) {
        GradeHistoryResponse response = new GradeHistoryResponse();
        response.setId(gh.getId());
        response.setAchievedSales(gh.getAchievedSales());
        response.setCalculatedGradeAmount(gh.getCalculatedGradeAmount());
        response.setPeriodStart(gh.getPeriodStart());
        response.setPeriodEnd(gh.getPeriodEnd());

        if (gh.getEmployee() != null)
            response.setEmployeeFullName(
                    gh.getEmployee().getFirstName() + " " + gh.getEmployee().getLastName());

        if (gh.getGradeStructure() != null) {
            response.setGradeName(gh.getGradeStructure().getGradeName());
            response.setAppliedThreshold(gh.getGradeStructure().getMinThreshold());
            response.setPeriodType(gh.getGradeStructure().getPeriodType().name());
        }

        if (gh.getPosition() != null)
            response.setPositionType(gh.getPosition().getPositionType().name());

        if (gh.getStore() != null)
            response.setStoreName(gh.getStore().getName());

        return response;
    }
}