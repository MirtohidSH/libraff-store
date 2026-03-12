package org.example.libraffstore.dto.request;

import lombok.Data;

import java.util.Map;

@Data
public class TransactionSaleRequest {

    private Long storeId;

    private Long employeeId;

    private Map<Long, Long> soldBooks;
}
