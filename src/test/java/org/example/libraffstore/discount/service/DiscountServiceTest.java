package org.example.libraffstore.discount.service;

import org.example.libraffstore.discount.dto.DiscountRequest;
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

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DiscountServiceTest {

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
    void createDiscount_whenValidRequest_shouldReturnDiscountResponse() {
        DiscountRequest request = buildRequest();
        Discount discount = new Discount();
        DiscountResponse response = new DiscountResponse();

        when(discountEntityBuilder.buildFrom(request)).thenReturn(discount);
        when(discountRepository.save(discount)).thenReturn(discount);
        when(discountMapper.toResponse(discount)).thenReturn(response);

        DiscountResponse result = discountService.createDiscount(request);

        assertThat(result).isEqualTo(response);

        verify(discountValidator).validateTargets(request);
        verify(discountValidator).validateDateRange(request.getStartDate(), request.getEndDate());
        verify(discountEntityBuilder).buildFrom(request);
        verify(discountRepository).save(discount);
        verify(discountMapper).toResponse(discount);
    }

    @Test
    void createDiscount_shouldCallValidateTargets_beforeBuildingEntity() {
        DiscountRequest request = buildRequest();
        Discount discount = new Discount();
        DiscountResponse response = new DiscountResponse();

        when(discountEntityBuilder.buildFrom(request)).thenReturn(discount);
        when(discountRepository.save(discount)).thenReturn(discount);
        when(discountMapper.toResponse(discount)).thenReturn(response);

        DiscountResponse result = discountService.createDiscount(request);
        assertThat(result).isEqualTo(response);

        verify(discountValidator).validateTargets(request);
        verify(discountValidator).validateDateRange(request.getStartDate(), request.getEndDate());
        verify(discountEntityBuilder).buildFrom(request);
        verify(discountRepository).save(discount);
        verify(discountMapper).toResponse(discount);
    }

    @Test
    void createDiscount_whenValidatorThrows_shouldPropagateException() {
        DiscountRequest request = buildRequest();
        doThrow(new RuntimeException("Validation failed"))
                .when(discountValidator).validateTargets(request);
        org.assertj.core.api.Assertions
                .assertThatThrownBy(() -> discountService.createDiscount(request))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Validation failed");

        verifyNoInteractions(discountEntityBuilder, discountRepository, discountMapper);
    }

    private DiscountRequest buildRequest() {
        DiscountRequest request = new DiscountRequest();
        request.setBookId(1L);
        request.setStartDate(LocalDate.of(2024, 1, 1));
        request.setEndDate(LocalDate.of(2024, 12, 31));
        return request;
    }

}
