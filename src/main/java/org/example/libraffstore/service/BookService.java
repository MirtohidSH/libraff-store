package org.example.libraffstore.service;

import lombok.RequiredArgsConstructor;
import org.example.libraffstore.dto.response.BookListResponse;
import org.example.libraffstore.dto.response.BookSingleResponse;
import org.example.libraffstore.entity.Book;
import org.example.libraffstore.repository.BookRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class BookService {

    private final BookRepository bookRepository;
    private final ModelMapper modelMapper;

    public BookListResponse findAll() {
        List<Book> books = bookRepository.findAll();
        List<BookSingleResponse> responseList = new ArrayList<BookSingleResponse>();

        for (Book book : books) {
            BookSingleResponse response = new BookSingleResponse();
            modelMapper.map(book, response);
            responseList.add(response);
        }

        BookListResponse listResponse = new BookListResponse();
        listResponse.setBooks(responseList);
        return listResponse;
    }


}
