package org.example.libraffstore.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.libraffstore.dto.request.DiscountRequest;
import org.example.libraffstore.dto.response.DiscountResponse;
import org.example.libraffstore.service.DiscountService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/discounts")
@RequiredArgsConstructor
public class DiscountController {

    private final DiscountService discountService;

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody DiscountRequest request){
        return ResponseEntity.status(HttpStatus.CREATED).body(discountService.createDiscount(request));
    }

    @GetMapping("/active")
    public ResponseEntity<List<DiscountResponse>> getActive(){
        return ResponseEntity.ok(discountService.findAllActive());
    }
}
