package org.example.libraffstore.validator;

import lombok.RequiredArgsConstructor;
import org.example.libraffstore.entity.Position;
import org.example.libraffstore.entity.Store;
import org.example.libraffstore.enums.PositionType;
import org.example.libraffstore.exception.BusinessException;
import org.example.libraffstore.repository.EmployeeRepository;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class PositionLimitValidator {

    private final EmployeeRepository employeeRepository;

    private static final Map<PositionType, Integer> POSITION_LIMITS = Map.of(
            PositionType.MANAGER, 1,
            PositionType.HEAD_SALES_REPRESENTATIVE, 2,
            PositionType.SALES_REPRESENTATIVE, 3,
            PositionType.CASHIER, 3
    );

    public void validatePositionLimit(Store store, Position position) {

        PositionType positionType = position.getPositionType();
        int limit = POSITION_LIMITS.getOrDefault(positionType, Integer.MAX_VALUE);

        long currentCount = employeeRepository
                .countByStoreIdAndPositionPositionTypeAndIsActiveTrue(
                        store.getId(), positionType);

        if (currentCount >= limit) {
            throw new BusinessException(String.format(
                    "'%s' mağazasında '%s' vəzifəsi üçün maksimum limit (%d) dolubdur.",
                    store.getName(), positionType.name(), limit));
        }
    }

    public void validatePositionLimitForTransfer(Store toStore, Position toPosition, Store fromStore, Position fromPosition) {

        boolean sameStoreAndPosition = toStore.getId().equals(fromStore.getId()) && toPosition.getPositionType() == fromPosition.getPositionType();

        if(sameStoreAndPosition) return;

        validatePositionLimit(toStore, toPosition);
    }
}
