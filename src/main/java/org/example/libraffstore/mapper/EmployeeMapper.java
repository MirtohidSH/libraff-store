package org.example.libraffstore.mapper;

import org.example.libraffstore.dto.response.EmployeeResponse;
import org.example.libraffstore.entity.Employee;
import org.springframework.stereotype.Component;

@Component
public class EmployeeMapper {

    public EmployeeResponse toResponse(Employee employee) {
        EmployeeResponse response = new EmployeeResponse();
        response.setId(employee.getId());
        response.setFIN(employee.getFIN());
        response.setFirstName(employee.getFirstName());
        response.setLastName(employee.getLastName());
        response.setEmail(employee.getEmail());
        response.setPhone(employee.getPhone());
        response.setIsActive(employee.getIsActive());
        response.setSalary(employee.getSalary());
        response.setDateEmployed(employee.getDateEmployed());
        response.setDateUnemployed(employee.getDateUnemployed());

        if (employee.getStore() != null) {
            response.setStoreName(employee.getStore().getName());
            response.setStoreAddress(employee.getStore().getAddress());
        }
        if (employee.getPosition() != null) {
            response.setPositionType(employee.getPosition().getPositionType());
        }
        return response;
    }
}
