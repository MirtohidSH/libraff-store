package org.example.libraffstore.finance.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.libraffstore.finance.dto.TransactionSaleRequest;
import org.example.libraffstore.finance.service.TransactionHistoryService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/transaction-histories")
@RequiredArgsConstructor
public class TransactionHistoryController {

    private final TransactionHistoryService transactionHistoryService;

    @PostMapping("/sell")
    @ResponseStatus(HttpStatus.OK)
    public void sellBook(@Valid @RequestBody TransactionSaleRequest request) {
        transactionHistoryService.sellBook(
                request.getSoldBooks(),
                request.getStoreId(),
                request.getEmployeeId()
        );
    }
}
