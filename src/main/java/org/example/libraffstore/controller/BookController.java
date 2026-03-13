package org.example.libraffstore.controller;

import lombok.RequiredArgsConstructor;
import org.example.libraffstore.dto.response.BookListResponse;
import org.example.libraffstore.service.BookService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping(path = "/books")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;

    @GetMapping
    public BookListResponse getEmployees() {
        return bookService.findAll();
    }
}
