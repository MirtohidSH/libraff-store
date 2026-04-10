package org.example.libraffstore.mapper;

import org.example.libraffstore.dto.response.DiscountResponse;
import org.example.libraffstore.entity.Discount;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface DiscountMapper {

    @Mapping(target = "bookName",   source = "book.name")
    @Mapping(target = "genreName",  source = "genre.name")
    @Mapping(target = "storeName",  source = "store.name")
    @Mapping(target = "authorName", expression = "java(mapAuthorName(discount))")
    DiscountResponse toResponse(Discount discount);

    default String mapAuthorName(Discount discount) {
        if (discount.getAuthor() == null) return null;
        return discount.getAuthor().getFirstName() + " " + discount.getAuthor().getLastName();
    }
}