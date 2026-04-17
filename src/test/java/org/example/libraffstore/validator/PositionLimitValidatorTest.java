package org.example.libraffstore.validator;

import org.example.libraffstore.entity.Position;
import org.example.libraffstore.entity.Store;
import org.example.libraffstore.enums.PositionType;
import org.example.libraffstore.exception.BusinessException;
import org.example.libraffstore.repository.EmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PositionLimitValidatorTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @InjectMocks
    private PositionLimitValidator positionLimitValidator;

    private Store store;

    @BeforeEach
    void setUp() {
        store = new Store();
        store.setId(1L);
        store.setName("Test Store");
    }

    // ── validatePositionLimit ──────────────────────────────────────────────────

    @Test
    void validatePositionLimit_whenManagerLimitReached_shouldThrowBusinessException() {
        Position position = buildPosition(PositionType.MANAGER);
        when(employeeRepository.countByStoreIdAndPositionPositionTypeAndIsActiveTrue(1L, PositionType.MANAGER))
                .thenReturn(1L); // limit is 1

        assertThatThrownBy(() -> positionLimitValidator.validatePositionLimit(store, position))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("MANAGER");
    }

    @Test
    void validatePositionLimit_whenManagerSlotAvailable_shouldNotThrow() {
        Position position = buildPosition(PositionType.MANAGER);
        when(employeeRepository.countByStoreIdAndPositionPositionTypeAndIsActiveTrue(1L, PositionType.MANAGER))
                .thenReturn(0L); // slot available

        assertThatCode(() -> positionLimitValidator.validatePositionLimit(store, position))
                .doesNotThrowAnyException();
    }

    @Test
    void validatePositionLimit_whenHeadSalesRepLimitReached_shouldThrow() {
        Position position = buildPosition(PositionType.HEAD_SALES_REPRESENTATIVE);
        when(employeeRepository.countByStoreIdAndPositionPositionTypeAndIsActiveTrue(1L, PositionType.HEAD_SALES_REPRESENTATIVE))
                .thenReturn(2L); // limit is 2

        assertThatThrownBy(() -> positionLimitValidator.validatePositionLimit(store, position))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    void validatePositionLimit_whenCashierLimitNotReached_shouldNotThrow() {
        Position position = buildPosition(PositionType.CASHIER);
        when(employeeRepository.countByStoreIdAndPositionPositionTypeAndIsActiveTrue(1L, PositionType.CASHIER))
                .thenReturn(2L); // limit is 3, still 1 slot left

        assertThatCode(() -> positionLimitValidator.validatePositionLimit(store, position))
                .doesNotThrowAnyException();
    }

    // ── validatePositionLimitForTransfer ──────────────────────────────────────

    @Test
    void validatePositionLimitForTransfer_whenSameStoreAndSamePosition_shouldSkipValidation() {
        Position samePosition = buildPosition(PositionType.SALES_REPRESENTATIVE);
        Store sameStore = store;

        // No repository call should be made – if it were called it would throw due to no stub
        assertThatCode(() -> positionLimitValidator.validatePositionLimitForTransfer(
                sameStore, samePosition, sameStore, samePosition))
                .doesNotThrowAnyException();
    }

    @Test
    void validatePositionLimitForTransfer_whenDifferentStore_shouldValidateLimit() {
        Position toPosition = buildPosition(PositionType.MANAGER);

        Store fromStore = new Store();
        fromStore.setId(99L);
        fromStore.setName("From Store");
        Position fromPosition = buildPosition(PositionType.SALES_REPRESENTATIVE);

        when(employeeRepository.countByStoreIdAndPositionPositionTypeAndIsActiveTrue(1L, PositionType.MANAGER))
                .thenReturn(1L); // limit is 1 → should throw

        assertThatThrownBy(() -> positionLimitValidator.validatePositionLimitForTransfer(
                store, toPosition, fromStore, fromPosition))
                .isInstanceOf(BusinessException.class);
    }

    // ── helpers ───────────────────────────────────────────────────────────────

    private Position buildPosition(PositionType type) {
        Position p = new Position();
        p.setId(1L);
        p.setPositionType(type);
        return p;
    }
}
