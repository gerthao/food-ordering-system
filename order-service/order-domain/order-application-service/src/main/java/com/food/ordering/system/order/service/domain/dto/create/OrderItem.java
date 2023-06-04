package com.food.ordering.system.order.service.domain.dto.create;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.UUID;

@Builder
@AllArgsConstructor
@ToString
public record OrderItem(
        @NotNull UUID productId,
        @NotNull Integer quantity,
        @NotNull BigDecimal price,
        @NotNull BigDecimal subtotal
) { }
