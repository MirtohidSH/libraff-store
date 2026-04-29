package org.example.libraffstore.catalog.mapper;

import org.example.libraffstore.catalog.dto.BookSingleResponse;
import org.example.libraffstore.catalog.entity.Book;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BookMapper {

    BookSingleResponse toResponse(Book book) ;
}

