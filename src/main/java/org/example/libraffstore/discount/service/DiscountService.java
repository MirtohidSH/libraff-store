package org.example.libraffstore.discount.service;

import lombok.RequiredArgsConstructor;
import org.example.libraffstore.discount.dto.DiscountRequest;
import org.example.libraffstore.discount.dto.DiscountResponse;
import org.example.libraffstore.discount.entity.Discount;
import org.example.libraffstore.discount.mapper.DiscountMapper;
import org.example.libraffstore.discount.repository.DiscountRepository;
import org.example.libraffstore.discount.service.helper.DiscountEntityBuilder;
import org.example.libraffstore.discount.validator.DiscountValidator;
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
