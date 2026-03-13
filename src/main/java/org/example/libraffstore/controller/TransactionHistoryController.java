package org.example.libraffstore.controller;

import lombok.RequiredArgsConstructor;
import org.example.libraffstore.dto.request.TransactionSaleRequest;
import org.example.libraffstore.service.TransactionHistoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping(path = "/transaction-histories")
@RequiredArgsConstructor
public class TransactionHistoryController {

    private final TransactionHistoryService service;

    @PostMapping(path = "/sell")
    public ResponseEntity<?> sellBook(@RequestBody TransactionSaleRequest request) {

        service.sellBook(request.getSoldBooks(), request.getStoreId(), request.getEmployeeId());
        return ResponseEntity.ok().build();
    }
}
