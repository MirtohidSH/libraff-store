package org.example.libraffstore.mapper;

import org.example.libraffstore.dto.response.BookSingleResponse;
import org.example.libraffstore.entity.Book;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BookMapper {

    BookSingleResponse toResponse(Book book) ;
}
