package org.example.libraffstore.validator;

import org.example.libraffstore.entity.Position;
import org.example.libraffstore.enums.PositionType;
import org.example.libraffstore.exception.AlreadyExistsException;
import org.example.libraffstore.exception.BusinessException;
import org.example.libraffstore.repository.EmployeeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmployeeValidatorTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @InjectMocks
    private EmployeeValidator employeeValidator;

    @Test
    void validateNewEmployee_whenFINAlreadyExists_shouldThrowAlreadyExistsException() {
        when(employeeRepository.existsByFIN("ABC123")).thenReturn(true);

        assertThatThrownBy(() -> employeeValidator.validateNewEmployee("ABC123", null, null))
                .isInstanceOf(AlreadyExistsException.class)
                .hasMessageContaining("ABC123");
    }

    @Test
    void validateNewEmployee_whenEmailAlreadyExists_shouldThrowAlreadyExistsException() {
        when(employeeRepository.existsByFIN("ABC123")).thenReturn(false);
        when(employeeRepository.existsByEmail("test@mail.com")).thenReturn(true);

        assertThatThrownBy(() -> employeeValidator.validateNewEmployee("ABC123", "test@mail.com", null))
                .isInstanceOf(AlreadyExistsException.class)
                .hasMessageContaining("test@mail.com");
    }

    @Test
    void validateNewEmployee_whenPhoneAlreadyExists_shouldThrowAlreadyExistsException() {
        when(employeeRepository.existsByFIN("ABC123")).thenReturn(false);
        when(employeeRepository.existsByEmail("test@mail.com")).thenReturn(false);
        when(employeeRepository.existsByPhone("0501234567")).thenReturn(true);

        assertThatThrownBy(() -> employeeValidator.validateNewEmployee("ABC123", "test@mail.com", "0501234567"))
                .isInstanceOf(AlreadyExistsException.class)
                .hasMessageContaining("0501234567");
    }

    @Test
    void validateNewEmployee_whenAllFieldsUnique_shouldNotThrow() {
        when(employeeRepository.existsByFIN("ABC123")).thenReturn(false);
        when(employeeRepository.existsByEmail("test@mail.com")).thenReturn(false);
        when(employeeRepository.existsByPhone("0501234567")).thenReturn(false);

        assertThatCode(() -> employeeValidator.validateNewEmployee("ABC123", "test@mail.com", "0501234567"))
                .doesNotThrowAnyException();
    }

    @Test
    void validateNewEmployee_whenEmailIsNull_shouldNotCheckEmail() {
        when(employeeRepository.existsByFIN("ABC123")).thenReturn(false);

        assertThatCode(() -> employeeValidator.validateNewEmployee("ABC123", null, null))
                .doesNotThrowAnyException();
    }

    @Test
    void validateUpdateEmployee_whenFINBelongsToAnotherEmployee_shouldThrow() {
        when(employeeRepository.existsByFINAndIdNot("ABC123", 1L)).thenReturn(true);

        assertThatThrownBy(() -> employeeValidator.validateUpdateEmployee(1L, "ABC123", null, null))
                .isInstanceOf(AlreadyExistsException.class)
                .hasMessageContaining("ABC123");
    }

    @Test
    void validateUpdateEmployee_whenEmailBelongsToAnotherEmployee_shouldThrow() {
        when(employeeRepository.existsByFINAndIdNot("ABC123", 1L)).thenReturn(false);
        when(employeeRepository.existsByEmailAndIdNot("a@b.com", 1L)).thenReturn(true);

        assertThatThrownBy(() -> employeeValidator.validateUpdateEmployee(1L, "ABC123", "a@b.com", null))
                .isInstanceOf(AlreadyExistsException.class)
                .hasMessageContaining("a@b.com");
    }

    @Test
    void validateUpdateEmployee_whenAllFieldsBelongToSameEmployee_shouldNotThrow() {
        when(employeeRepository.existsByFINAndIdNot("ABC123", 1L)).thenReturn(false);
        when(employeeRepository.existsByEmailAndIdNot("a@b.com", 1L)).thenReturn(false);
        when(employeeRepository.existsByPhoneAndIdNot("050", 1L)).thenReturn(false);

        assertThatCode(() -> employeeValidator.validateUpdateEmployee(1L, "ABC123", "a@b.com", "050"))
                .doesNotThrowAnyException();
    }

    @Test
    void validateSalaryRange_whenSalaryBelowMin_shouldThrowBusinessException() {
        Position position = buildPosition(1000, 2000);

        assertThatThrownBy(() -> employeeValidator.validateSalaryRange(new BigDecimal("500"), position))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("minimum");
    }

    @Test
    void validateSalaryRange_whenSalaryAboveMax_shouldThrowBusinessException() {
        Position position = buildPosition(1000, 2000);

        assertThatThrownBy(() -> employeeValidator.validateSalaryRange(new BigDecimal("3000"), position))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("maksimum");
    }

    @Test
    void validateSalaryRange_whenSalaryWithinRange_shouldNotThrow() {
        Position position = buildPosition(1000, 2000);

        assertThatCode(() -> employeeValidator.validateSalaryRange(new BigDecimal("1500"), position))
                .doesNotThrowAnyException();
    }

    @Test
    void validateSalaryRange_whenSalaryEqualsMin_shouldNotThrow() {
        Position position = buildPosition(1000, 2000);

        assertThatCode(() -> employeeValidator.validateSalaryRange(new BigDecimal("1000"), position))
                .doesNotThrowAnyException();
    }

    @Test
    void validateSalaryRange_whenSalaryEqualsMax_shouldNotThrow() {
        Position position = buildPosition(1000, 2000);

        assertThatCode(() -> employeeValidator.validateSalaryRange(new BigDecimal("2000"), position))
                .doesNotThrowAnyException();
    }

    @Test
    void validateSalaryRange_whenPositionHasNoMinSalary_shouldNotThrowForLowSalary() {
        Position position = buildPosition(null, 3000);

        assertThatCode(() -> employeeValidator.validateSalaryRange(new BigDecimal("100"), position))
                .doesNotThrowAnyException();
    }

    @Test
    void validateSalaryRange_whenPositionHasNoMaxSalary_shouldNotThrowForHighSalary() {
        Position position = buildPosition(1000, null);

        assertThatCode(() -> employeeValidator.validateSalaryRange(new BigDecimal("99999"), position))
                .doesNotThrowAnyException();
    }

    private Position buildPosition(Integer min, Integer max) {
        Position position = new Position();
        position.setId(1L);
        position.setPositionType(PositionType.SALES_REPRESENTATIVE);
        position.setMinSalary(min);
        position.setMaxSalary(max);
        return position;
    }
}
