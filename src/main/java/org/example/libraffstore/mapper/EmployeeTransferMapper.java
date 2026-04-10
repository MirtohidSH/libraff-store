package org.example.libraffstore.mapper;

import org.example.libraffstore.dto.EmployeeTransferContext;
import org.example.libraffstore.dto.response.EmployeeTransferResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface EmployeeTransferMapper {

    @Mapping(target = "employeeId", source = "employee.id")
    @Mapping(target = "firstName", source = "employee.firstName")
    @Mapping(target = "lastName", source = "employee.lastName")
    @Mapping(target = "fromStoreName", source = "fromStore.name")
    @Mapping(target = "toStoreName", source = "toStore.name")
    @Mapping(target = "fromPositionType", source = "fromPosition.positionType")
    @Mapping(target = "toPositionType", source = "toPosition.positionType")
    @Mapping(target = "fromSalary", source = "fromSalary")
    @Mapping(target = "toSalary", source = "toSalary")
    @Mapping(target = "transferDate", source = "transferDate")
    EmployeeTransferResponse toResponse(EmployeeTransferContext context);
}