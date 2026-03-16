package org.example.libraffstore.service;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.example.libraffstore.dto.request.DiscountRequest;
import org.example.libraffstore.dto.response.DiscountResponse;
import org.example.libraffstore.entity.Author;
import org.example.libraffstore.entity.Book;
import org.example.libraffstore.entity.Discount;
import org.example.libraffstore.exception.BusinessException;
import org.example.libraffstore.exception.NotFoundException;
import org.example.libraffstore.repository.*;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DiscountService {

    private final DiscountRepository discountRepository;
    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;
    private final GenreRepository genreRepository;
    private final StoreRepository storeRepository;

    @Transactional
    public DiscountResponse createDiscount(DiscountRequest request) {

        validateDiscountTargets(request);

        if (request.getStartDate().isAfter(request.getEndDate()))
            throw new BusinessException("Başlama tarixi bitmə tarixindən sonra ola bilməz.");

        Discount discount = new Discount();
        discount.setName(request.getName());
        discount.setDiscountPercentage(request.getDiscountPercentage());
        discount.setStartDate(request.getStartDate());
        discount.setEndDate(request.getEndDate());
        discount.setIsActive(request.getIsActive() != null ? request.getIsActive() : true);

        if (request.getBookId() != null) {
            discount.setBook(bookRepository.findById(request.getBookId())
                    .orElseThrow(() -> new NotFoundException("Kitab tapılmadı. ID: " + request.getBookId())));
        }

        if (request.getAuthorId() != null) {
            discount.setAuthor(authorRepository.findById(request.getAuthorId())
                    .orElseThrow(() -> new NotFoundException("Müəllif tapılmadı. ID: " + request.getAuthorId())));
        }
        if (request.getGenreId() != null) {
            discount.setGenre(genreRepository.findById(request.getGenreId())
                    .orElseThrow(() -> new NotFoundException("Janr tapılmadı. ID: " + request.getGenreId())));
        }
        if (request.getStoreId() != null) {
            discount.setStore(storeRepository.findById(request.getStoreId())
                    .orElseThrow(() -> new NotFoundException("Mağaza tapılmadı. ID: " + request.getStoreId())));
        }
        return toResponse(discountRepository.save(discount));
    }

    public List<DiscountResponse> findAllActive() {
        return discountRepository.findByIsActiveTrue()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public BigDecimal calculateDiscountedPrice(Book book , Long storeId) {

        List<Long> authorIds = book.getAuthors() != null
                ? book.getAuthors().stream().map(Author::getId).toList()
                : new ArrayList<>();

        Long genreId = book.getGenre() != null ? book.getGenre().getId() : null;

        List<Discount> discounts = discountRepository.findApplicableDiscounts(
                book.getId(), authorIds, genreId, storeId, LocalDate.now()
        );

        if(discounts == null || discounts.isEmpty())
            return book.getSalesPrice();

        BigDecimal discountPrice = discounts.get(0).getDiscountPercentage();

        return book.getSalesPrice().multiply(BigDecimal.valueOf(100).subtract(discountPrice)
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP));
    }


    private void validateDiscountTargets(DiscountRequest request) {
        int filledCount = 0;

        if(request.getBookId() != null) filledCount++;
        if(request.getAuthorId() != null) filledCount++;
        if(request.getGenreId() != null) filledCount++;
        if(request.getStoreId() != null) filledCount++;

        if(filledCount == 0)
            throw new BusinessException("Endirim kitab, müəllif, janr və ya mağazadan birinə tətbiq edilməlidir.");
        if(filledCount > 1)
            throw new BusinessException("Endirim yalnız bir hədəfə tətbiq edilə bilər.");
    }

    private DiscountResponse toResponse(Discount discount) {
        DiscountResponse response = new DiscountResponse();
        response.setId(discount.getId());
        response.setName(discount.getName());
        response.setDiscountPercentage(discount.getDiscountPercentage());
        response.setStartDate(discount.getStartDate());
        response.setEndDate(discount.getEndDate());
        response.setIsActive(discount.getIsActive());

        if (discount.getBook() != null)
            response.setBookName(discount.getBook().getName());
        if (discount.getAuthor() != null)
            response.setAuthorName(discount.getAuthor().getFirstName() + " " + discount.getAuthor().getLastName());
        if (discount.getGenre() != null)
            response.setGenreName(discount.getGenre().getName());
        if (discount.getStore() != null)
            response.setStoreName(discount.getStore().getName());

        return response;
    }
}
