package org.example.libraffstore.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Map;

@Data
public class TransactionSaleRequest {

    @NotNull(message = "Mağaza ID boş ola bilməz")
    private Long storeId;

    @NotNull(message = "İşçi ID boş ola bilməz")
    private Long employeeId;

    @NotNull(message = "Satış siyahısı boş ola bilməz")
    @NotEmpty(message = "Ən azı bir kitab seçilməlidir")
    private Map<Long, Long> soldBooks;
}