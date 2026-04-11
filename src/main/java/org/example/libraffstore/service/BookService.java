package org.example.libraffstore.service;

import lombok.RequiredArgsConstructor;
import org.example.libraffstore.dto.response.BookListResponse;
import org.example.libraffstore.dto.response.BookSingleResponse;
import org.example.libraffstore.entity.Book;
import org.example.libraffstore.repository.BookRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;

    public BookListResponse findAll() {
        List<BookSingleResponse> books = bookRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();

        return new BookListResponse(books);
    }

    private BookSingleResponse toResponse(Book book) {
        BookSingleResponse response = new BookSingleResponse();
        response.setId(Math.toIntExact(book.getId()));
        response.setName(book.getName());
        return response;
    }
}