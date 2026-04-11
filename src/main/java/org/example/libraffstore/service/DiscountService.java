package org.example.libraffstore.service;

import lombok.RequiredArgsConstructor;
import org.example.libraffstore.dto.request.DiscountRequest;
import org.example.libraffstore.dto.response.DiscountResponse;
import org.example.libraffstore.entity.Discount;
import org.example.libraffstore.mapper.DiscountMapper;
import org.example.libraffstore.repository.DiscountRepository;
import org.example.libraffstore.service.helper.DiscountEntityBuilder;
import org.example.libraffstore.validator.DiscountValidator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DiscountService {

    private final DiscountRepository discountRepository;
    private final DiscountMapper discountMapper;
    private final DiscountValidator discountValidator;
    private final DiscountEntityBuilder discountEntityBuilder;

    @Transactional
    public DiscountResponse createDiscount(DiscountRequest request) {
        discountValidator.validateTargets(request);
        discountValidator.validateDateRange(request.getStartDate(), request.getEndDate());

        Discount discount = discountEntityBuilder.buildFrom(request);
        return discountMapper.toResponse(discountRepository.save(discount));
    }

    public List<DiscountResponse> findAllActive() {
        return discountRepository.findByIsActiveTrue()
                .stream()
                .map(discountMapper::toResponse)
                .toList();
    }
}