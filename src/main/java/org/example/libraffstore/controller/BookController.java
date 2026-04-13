package org.example.libraffstore.controller;

import lombok.RequiredArgsConstructor;
import org.example.libraffstore.dto.response.BookSingleResponse;
import org.example.libraffstore.service.BookService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping(path = "/books")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;

    @GetMapping
    public ResponseEntity<List<BookSingleResponse>> getAllBooks() {
        return ResponseEntity.ok(bookService.findAll());
    }
}
