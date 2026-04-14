package org.example.libraffstore.service;

import org.example.libraffstore.dto.response.BookSingleResponse;
import org.example.libraffstore.entity.Book;
import org.example.libraffstore.mapper.BookMapper;
import org.example.libraffstore.repository.BookRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private BookMapper bookMapper;

    @InjectMocks
    private BookService bookService;

    @Test
    void findAll_ShouldReturnBookSingleResponseList() {
        Book book = new Book();
        book.setId(1L);
        book.setName("Test Book");
        book.setSalesPrice(new BigDecimal("15.50"));
        book.setPurchasePrice(new BigDecimal("10.00"));
        book.setDatePublished(LocalDate.of(1999, 1, 1));

        BookSingleResponse response = new BookSingleResponse(1L, "Test Book");

        when(bookRepository.findAll()).thenReturn(List.of(book));
        when(bookMapper.toResponse(book)).thenReturn(response);

        List<BookSingleResponse> actualResponse = bookService.findAll();

        assertThat(actualResponse).hasSize(1).containsExactly(response);
        verify(bookRepository, times(1)).findAll();
        verify(bookMapper, times(1)).toResponse(book);
    }

    @Test
    void findAll_WhenRepositoryReturnsEmpty_ShouldReturnEmptyList() {
        when(bookRepository.findAll()).thenReturn(Collections.emptyList());

        List<BookSingleResponse> actualResponse = bookService.findAll();

        assertThat(actualResponse).isEmpty();
        verify(bookRepository, times(1)).findAll();
        verifyNoInteractions(bookMapper);
    }
}