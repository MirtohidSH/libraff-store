package org.example.libraffstore.catalog.service;

import lombok.RequiredArgsConstructor;
import org.example.libraffstore.catalog.dto.BookSingleResponse;
import org.example.libraffstore.catalog.mapper.BookMapper;
import org.example.libraffstore.catalog.repository.BookRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;
    private final BookMapper bookMapper;

    public List<BookSingleResponse> findAll() {
        return bookRepository.findAll()
                .stream()
                .map(bookMapper::toResponse)
                .toList();
    }
}