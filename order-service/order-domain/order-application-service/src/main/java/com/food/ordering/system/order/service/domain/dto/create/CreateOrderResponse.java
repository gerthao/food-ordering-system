package com.food.ordering.system.order.service.domain.dto.create;

import com.food.ordering.system.order.service.domain.valueobject.OrderStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.ToString;

import java.util.UUID;

@Builder
@AllArgsConstructor
@ToString
public record CreateOrderResponse(
        @NotNull UUID orderTrackingId,
        @NotNull OrderStatus orderStatus,
        @NotNull String message
) { }
