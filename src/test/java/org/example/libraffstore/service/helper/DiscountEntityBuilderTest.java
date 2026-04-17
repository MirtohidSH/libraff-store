package org.example.libraffstore.service.helper;

import org.example.libraffstore.dto.request.DiscountRequest;
import org.example.libraffstore.entity.Author;
import org.example.libraffstore.entity.Book;
import org.example.libraffstore.entity.Discount;
import org.example.libraffstore.entity.Genre;
import org.example.libraffstore.entity.Store;
import org.example.libraffstore.exception.NotFoundException;
import org.example.libraffstore.repository.AuthorRepository;
import org.example.libraffstore.repository.BookRepository;
import org.example.libraffstore.repository.GenreRepository;
import org.example.libraffstore.repository.StoreRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DiscountEntityBuilderTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private AuthorRepository authorRepository;

    @Mock
    private GenreRepository genreRepository;

    @Mock
    private StoreRepository storeRepository;

    @InjectMocks
    private DiscountEntityBuilder discountEntityBuilder;

    private DiscountRequest baseRequest;

    @BeforeEach
    void setUp() {
        baseRequest = new DiscountRequest();
        baseRequest.setName("Yaz Endirimi");
        baseRequest.setDiscountPercentage(new BigDecimal("10"));
        baseRequest.setStartDate(LocalDate.of(2024, 3, 1));
        baseRequest.setEndDate(LocalDate.of(2024, 3, 31));
        baseRequest.setIsActive(true);
    }

    @Test
    void buildFrom_whenBookIdProvided_shouldSetBook() {
        Book book = new Book();
        book.setId(1L);
        baseRequest.setBookId(1L);

        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        Discount discount = discountEntityBuilder.buildFrom(baseRequest);

        assertThat(discount.getBook()).isEqualTo(book);
        assertThat(discount.getName()).isEqualTo("Yaz Endirimi");
        verify(bookRepository).findById(1L);
    }

    @Test
    void buildFrom_whenBookIdNotFound_shouldThrowNotFoundException() {
        baseRequest.setBookId(99L);
        when(bookRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> discountEntityBuilder.buildFrom(baseRequest))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void buildFrom_whenAuthorIdProvided_shouldSetAuthor() {
        Author author = new Author();
        author.setId(2L);
        baseRequest.setAuthorId(2L);

        when(authorRepository.findById(2L)).thenReturn(Optional.of(author));

        Discount discount = discountEntityBuilder.buildFrom(baseRequest);

        assertThat(discount.getAuthor()).isEqualTo(author);
        verify(authorRepository).findById(2L);
    }

    @Test
    void buildFrom_whenAuthorIdNotFound_shouldThrowNotFoundException() {
        baseRequest.setAuthorId(99L);
        when(authorRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> discountEntityBuilder.buildFrom(baseRequest))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void buildFrom_whenGenreIdProvided_shouldSetGenre() {
        Genre genre = new Genre();
        genre.setId(3L);
        baseRequest.setGenreId(3L);

        when(genreRepository.findById(3L)).thenReturn(Optional.of(genre));

        Discount discount = discountEntityBuilder.buildFrom(baseRequest);

        assertThat(discount.getGenre()).isEqualTo(genre);
    }

    @Test
    void buildFrom_whenStoreIdProvided_shouldSetStore() {
        Store store = new Store();
        store.setId(4L);
        baseRequest.setStoreId(4L);

        when(storeRepository.findById(4L)).thenReturn(Optional.of(store));

        Discount discount = discountEntityBuilder.buildFrom(baseRequest);

        assertThat(discount.getStore()).isEqualTo(store);
    }

    @Test
    void buildFrom_whenNoTargetIdProvided_shouldBuildDiscountWithoutAssociations() {
        Discount discount = discountEntityBuilder.buildFrom(baseRequest);

        assertThat(discount.getName()).isEqualTo("Yaz Endirimi");
        assertThat(discount.getDiscountPercentage()).isEqualByComparingTo("10");
        assertThat(discount.getStartDate()).isEqualTo(LocalDate.of(2024, 3, 1));
        assertThat(discount.getIsActive()).isTrue();
        assertThat(discount.getBook()).isNull();
        assertThat(discount.getAuthor()).isNull();
        assertThat(discount.getGenre()).isNull();
        assertThat(discount.getStore()).isNull();

        verifyNoInteractions(bookRepository, authorRepository, genreRepository, storeRepository);
    }

    @Test
    void buildFrom_whenIsActiveIsNull_shouldDefaultToTrue() {
        baseRequest.setIsActive(null);

        Discount discount = discountEntityBuilder.buildFrom(baseRequest);

        assertThat(discount.getIsActive()).isTrue();
    }

    @Test
    void buildFrom_whenIsActiveIsFalse_shouldSetFalse() {
        baseRequest.setIsActive(false);

        Discount discount = discountEntityBuilder.buildFrom(baseRequest);

        assertThat(discount.getIsActive()).isFalse();
    }
}
