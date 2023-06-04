package com.food.ordering.system.order.service.domain.dto.create;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.ToString;

@Builder
@AllArgsConstructor
@ToString
public record OrderAddress(
        @NotNull @Max(value = 100) String street,
        @NotNull @Max(value = 10) String postalCode,
        @NotNull @Max(value = 100) String city
) { }
