package org.example.libraffstore.dto.response;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.libraffstore.enums.TransferStatus;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class BookTransferResponse {

    private Long id;
    private String bookName;
    private String fromStore;
    private String toStore;
    private String  requestedEmployee;
    private String approvedEmployee;
    private Integer quantity;
    private LocalDateTime requestedAt;
    private LocalDateTime approvedAt;
    private LocalDateTime completedAt;
    private TransferStatus transferStatus;
}
