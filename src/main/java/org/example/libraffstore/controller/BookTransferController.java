package org.example.libraffstore.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.libraffstore.dto.request.BookTransferRequest;
import org.example.libraffstore.dto.response.BookTransferResponse;
import org.example.libraffstore.dto.response.StoreBookStockResponse;
import org.example.libraffstore.service.BookTransferService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/book-transfers")
@RequiredArgsConstructor
public class BookTransferController {

    private final BookTransferService bookTransferService;

    @GetMapping("/available/{bookId}")
    public ResponseEntity<List<StoreBookStockResponse>> getAvailableStocks(@PathVariable Long bookId) {
        return ResponseEntity.ok(bookTransferService.getAvailableStocks(bookId));
    }

    @PostMapping
    public ResponseEntity<BookTransferResponse> createRequest(@Valid @RequestBody BookTransferRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(bookTransferService.createTransferRequest(request));
    }

    @GetMapping("/pending")
    public ResponseEntity<List<BookTransferResponse>> getPending() {
        return ResponseEntity.ok(bookTransferService.getPendingTransfers());
    }

    @PatchMapping("/{id}/approve")
    public ResponseEntity<BookTransferResponse> approve(@PathVariable Long id, @RequestParam Long managerId) {
        return ResponseEntity.ok(bookTransferService.approveTransfer(id, managerId));
    }

    @PatchMapping("/{id}/reject")
    public ResponseEntity<BookTransferResponse> reject(@PathVariable Long id, @RequestParam Long managerId) {
        return ResponseEntity.ok(bookTransferService.rejectTransfer(id, managerId));
    }
}