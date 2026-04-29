package org.example.libraffstore.inventory.mapper;

import org.example.libraffstore.inventory.dto.BookTransferResponse;
import org.example.libraffstore.inventory.dto.StoreBookStockResponse;
import org.example.libraffstore.inventory.entity.BookTransfer;
import org.example.libraffstore.inventory.entity.StoreBookStock;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BookTransferMapper {

    @Mapping(target = "bookName",  source = "book.name")
    @Mapping(target = "fromStore", source = "fromStore.name")
    @Mapping(target = "toStore",   source = "toStore.name")
    @Mapping(target = "requestedEmployee",
            expression = "java(transfer.getRequestedEmployee().getFirstName() + ' ' + transfer.getRequestedEmployee().getLastName())")
    @Mapping(target = "approvedEmployee",
            expression = "java(transfer.getApprovedEmployee() == null ? null : transfer.getApprovedEmployee().getFirstName() + ' ' + transfer.getApprovedEmployee().getLastName())")
    BookTransferResponse toResponse(BookTransfer transfer);

    @Mapping(target = "storeId",   source = "store.id")
    @Mapping(target = "storeName", source = "store.name")
    @Mapping(target = "bookName",  source = "book.name")
    StoreBookStockResponse toStockResponse(StoreBookStock stock);
}

