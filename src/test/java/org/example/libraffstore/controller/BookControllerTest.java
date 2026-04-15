package org.example.libraffstore.controller;

import org.example.libraffstore.dto.response.BookSingleResponse;
import org.example.libraffstore.filters.JwtAuthenticationFilter;
import org.example.libraffstore.service.BookService;
import org.example.libraffstore.service.UserDetailsServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookController.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("BookController tests")
class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BookService bookService;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockitoBean
    private UserDetailsServiceImpl userDetailsService;

    private BookSingleResponse buildBook(Long id, String name) {
        return new BookSingleResponse(id, name);
    }

    @Test
    @DisplayName("GET /books → 200 və kitab listini qaytarır")
    void getAllBooks_whenBooksExist_returns200WithList() throws Exception {

        var books = List.of(
                buildBook(1L, "Clean Code"),
                buildBook(2L, "Refactoring")
        );
        when(bookService.findAll()).thenReturn(books);

        mockMvc.perform(get("/books")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("Clean Code")))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].name", is("Refactoring")));

        verify(bookService, times(1)).findAll();
        verifyNoMoreInteractions(bookService);
    }

    @Test
    @DisplayName("GET /books → boş list olduqda 200 və [] qaytarır")
    void getAllBooks_whenNoBooksExist_returns200WithEmptyList() throws Exception {
        when(bookService.findAll()).thenReturn(List.of());

        mockMvc.perform(get("/books"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)))
                .andExpect(jsonPath("$", empty()));

        verify(bookService, times(1)).findAll();
    }

    @Test
    @DisplayName("GET /books → tək kitab olduqda düzgün struktur qaytarır")
    void getAllBooks_whenOneBook_returnsCorrectStructure() throws Exception {
        when(bookService.findAll()).thenReturn(List.of(buildBook(99L, "DDD")));

        mockMvc.perform(get("/books"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").exists())
                .andExpect(jsonPath("$[0].name").exists())
                .andExpect(jsonPath("$[0].id", is(99)))
                .andExpect(jsonPath("$[0].name", is("DDD")));
    }

    @Test
    @DisplayName("GET /books → service exception atdıqda 500 qaytarır")
    void getAllBooks_whenServiceThrows_returns500() throws Exception {
        when(bookService.findAll()).thenThrow(new RuntimeException("DB connection failed"));

        mockMvc.perform(get("/books"))
                .andExpect(status().isInternalServerError());

        verify(bookService, times(1)).findAll();
    }
}