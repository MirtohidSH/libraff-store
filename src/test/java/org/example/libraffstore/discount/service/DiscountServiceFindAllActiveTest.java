package org.example.libraffstore.discount.service;

import org.example.libraffstore.discount.dto.DiscountResponse;
import org.example.libraffstore.discount.entity.Discount;
import org.example.libraffstore.discount.mapper.DiscountMapper;
import org.example.libraffstore.discount.repository.DiscountRepository;
import org.example.libraffstore.discount.service.helper.DiscountEntityBuilder;
import org.example.libraffstore.discount.validator.DiscountValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DiscountServiceFindAllActiveTest {

    @Mock
    private DiscountRepository discountRepository;

    @Mock
    private DiscountMapper discountMapper;

    @Mock
    private DiscountValidator discountValidator;

    @Mock
    private DiscountEntityBuilder discountEntityBuilder;

    @InjectMocks
    private DiscountService discountService;

    @Test
    void findAllActive_whenActiveDiscountsExist_shouldReturnMappedList() {
        Discount discount = new Discount();
        DiscountResponse response = new DiscountResponse();

        when(discountRepository.findByIsActiveTrue()).thenReturn(List.of(discount));
        when(discountMapper.toResponse(discount)).thenReturn(response);

        List<DiscountResponse> result = discountService.findAllActive();

        assertThat(result).hasSize(1).containsExactly(response);
        verify(discountRepository).findByIsActiveTrue();
        verify(discountMapper).toResponse(discount);
    }

    @Test
    void findAllActive_whenNoActiveDiscounts_shouldReturnEmptyList() {
        when(discountRepository.findByIsActiveTrue()).thenReturn(Collections.emptyList());

        List<DiscountResponse> result = discountService.findAllActive();

        assertThat(result).isEmpty();
        verify(discountRepository).findByIsActiveTrue();
        verifyNoInteractions(discountMapper);
    }

    @Test
    void findAllActive_whenMultipleActiveDiscounts_shouldReturnAllMapped() {
        Discount d1 = new Discount();
        Discount d2 = new Discount();
        DiscountResponse r1 = new DiscountResponse();
        DiscountResponse r2 = new DiscountResponse();

        when(discountRepository.findByIsActiveTrue()).thenReturn(List.of(d1, d2));
        when(discountMapper.toResponse(d1)).thenReturn(r1);
        when(discountMapper.toResponse(d2)).thenReturn(r2);

        List<DiscountResponse> result = discountService.findAllActive();

        assertThat(result).hasSize(2).containsExactly(r1, r2);
    }
}

