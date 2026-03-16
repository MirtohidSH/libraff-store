package org.example.libraffstore.dto.response;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class StoreBookStockResponse {
    private Long storeId;
    private String storeName;
    private String bookName;
    private Integer quantity;
}
