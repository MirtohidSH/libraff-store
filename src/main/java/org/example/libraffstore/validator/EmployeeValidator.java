package org.example.libraffstore.validator;

import lombok.RequiredArgsConstructor;
import org.example.libraffstore.entity.Position;
import org.example.libraffstore.exception.AlreadyExistsException;
import org.example.libraffstore.exception.BusinessException;
import org.example.libraffstore.repository.EmployeeRepository;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class EmployeeValidator {

    private final EmployeeRepository employeeRepository;

    public void validateNewEmployee(String FIN, String email, String phone) {
        if (employeeRepository.existsByFIN(FIN))
            throw new AlreadyExistsException("Bu FIN ilə işçi artıq mövcuddur: " + FIN);

        if (email != null && employeeRepository.existsByEmail(email))
            throw new AlreadyExistsException("Bu email artıq istifadə olunur: " + email);

        if (phone != null && employeeRepository.existsByPhone(phone))
            throw new AlreadyExistsException("Bu telefon nömrəsi artıq istifadə olunur: " + phone);
    }

    public void validateUpdateEmployee(Long employeeId, String FIN, String email, String phone) {
        if (employeeRepository.existsByFINAndIdNot(FIN, employeeId))
            throw new AlreadyExistsException("Bu FIN ilə işçi artıq mövcuddur: " + FIN);

        if (email != null && employeeRepository.existsByEmailAndIdNot(email, employeeId))
            throw new AlreadyExistsException("Bu email artıq istifadə olunur: " + email);

        if (phone != null && employeeRepository.existsByPhoneAndIdNot(phone, employeeId))
            throw new AlreadyExistsException("Bu telefon nömrəsi artıq istifadə olunur: " + phone);
    }

    public void validateSalaryRange(BigDecimal salary, Position position) {
        if (position.getMinSalary() != null
                && salary.compareTo(BigDecimal.valueOf(position.getMinSalary())) < 0) {
            throw new BusinessException(String.format(
                    "%s vəzifəsi üçün minimum maaş %d AZN-dir. Daxil edilən: %.2f AZN",
                    position.getPositionType().name(), position.getMinSalary(), salary));
        }
        if (position.getMaxSalary() != null
                && salary.compareTo(BigDecimal.valueOf(position.getMaxSalary())) > 0) {
            throw new BusinessException(String.format(
                    "%s vəzifəsi üçün maksimum maaş %d AZN-dir. Daxil edilən: %.2f AZN",
                    position.getPositionType().name(), position.getMaxSalary(), salary));
        }
    }
}