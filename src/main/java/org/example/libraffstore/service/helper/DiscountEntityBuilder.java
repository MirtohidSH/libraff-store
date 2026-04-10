package org.example.libraffstore.service.helper;

import lombok.RequiredArgsConstructor;
import org.example.libraffstore.dto.request.DiscountRequest;
import org.example.libraffstore.entity.Discount;
import org.example.libraffstore.exception.NotFoundException;
import org.example.libraffstore.repository.AuthorRepository;
import org.example.libraffstore.repository.BookRepository;
import org.example.libraffstore.repository.GenreRepository;
import org.example.libraffstore.repository.StoreRepository;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DiscountEntityBuilder {

    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;
    private final GenreRepository genreRepository;
    private final StoreRepository storeRepository;

    public Discount buildFrom(DiscountRequest request) {
        Discount discount = new Discount();
        discount.setName(request.getName());
        discount.setDiscountPercentage(request.getDiscountPercentage());
        discount.setStartDate(request.getStartDate());
        discount.setEndDate(request.getEndDate());
        discount.setIsActive(Boolean.TRUE.equals(request.getIsActive()) || request.getIsActive() == null);

        resolveAssociations(discount, request);
        return discount;
    }

    private void resolveAssociations(Discount discount, DiscountRequest request) {
        if (request.getBookId() != null)
            discount.setBook(bookRepository.findById(request.getBookId())
                    .orElseThrow(() -> new NotFoundException("Kitab tapılmadı. ID: " + request.getBookId())));

        if (request.getAuthorId() != null)
            discount.setAuthor(authorRepository.findById(request.getAuthorId())
                    .orElseThrow(() -> new NotFoundException("Müəllif tapılmadı. ID: " + request.getAuthorId())));

        if (request.getGenreId() != null)
            discount.setGenre(genreRepository.findById(request.getGenreId())
                    .orElseThrow(() -> new NotFoundException("Janr tapılmadı. ID: " + request.getGenreId())));

        if (request.getStoreId() != null)
            discount.setStore(storeRepository.findById(request.getStoreId())
                    .orElseThrow(() -> new NotFoundException("Mağaza tapılmadı. ID: " + request.getStoreId())));
    }
}
