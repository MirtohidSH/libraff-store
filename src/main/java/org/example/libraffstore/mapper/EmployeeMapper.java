package org.example.libraffstore.mapper;

import org.example.libraffstore.dto.request.EmployeeRequest;
import org.example.libraffstore.dto.response.EmployeeResponse;
import org.example.libraffstore.entity.Employee;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface EmployeeMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "isActive", constant = "true")
    @Mapping(target = "dateUnemployed", ignore = true)
    @Mapping(target = "store", ignore = true)
    @Mapping(target = "position", ignore = true)
    @Mapping(target = "roles", ignore = true)
    Employee toEntity(EmployeeRequest request);

    @Mapping(target = "storeName",    source = "store.name")
    @Mapping(target = "storeAddress", source = "store.address")
    @Mapping(target = "positionType", source = "position.positionType")
    EmployeeResponse toResponse(Employee employee);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEmployeeFromRequest(EmployeeRequest request, @MappingTarget Employee employee);
}