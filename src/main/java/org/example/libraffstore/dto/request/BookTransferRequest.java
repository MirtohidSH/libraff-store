package org.example.libraffstore.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class BookTransferRequest {

    @NotNull(message = "Kitab ID boş ola bilməz")
    private Long bookId;

    @NotNull(message = "Kitabın olduğu mağaza ID boş ola bilməz")
    private Long fromStoreId;

    @NotNull(message = "Kitabın göndəriləcəyi mağaza ID boş ola bilməz")
    private Long toStoreId;

    @NotNull(message = "Sorğu edən işçi ID boş ola bilməz")
    private Long requestedEmployeeId;

    @NotNull(message = "Miqdar boş ola bilməz")
    @Min(value = 1, message = "Miqdar minimum 1 olmalıdır")
    private Integer quantity;
}