package org.example.libraffstore.employee.mapper;

import org.example.libraffstore.employee.dto.GradeHistoryResponse;
import org.example.libraffstore.employee.entity.GradeHistory;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface GradeHistoryMapper {

    @Mapping(target = "employeeFullName",
            expression = "java(gh.getEmployee() == null ? null : gh.getEmployee().getFirstName() + ' ' + gh.getEmployee().getLastName())")
    @Mapping(target = "gradeName",        source = "gradeStructure.gradeName")
    @Mapping(target = "appliedThreshold", source = "gradeStructure.minThreshold")
    @Mapping(target = "periodType",       expression = "java(gh.getGradeStructure() == null ? null : gh.getGradeStructure().getPeriodType().name())")
    @Mapping(target = "positionType",     expression = "java(gh.getPosition() == null ? null : gh.getPosition().getPositionType().name())")
    @Mapping(target = "storeName",        source = "store.name")
    GradeHistoryResponse toResponse(GradeHistory gh);
}
