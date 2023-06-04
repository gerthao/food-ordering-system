package com.food.ordering.system.order.service.domain.dto.message;

import com.food.ordering.system.order.service.domain.valueobject.OrderApprovalStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.ToString;

import java.time.Instant;

@Builder
@AllArgsConstructor
@ToString
public record RestaurantApprovalResponse(
        String id,
        String sagaId,
        String orderId,
        String restaurantId,
        Instant createdAt,
        OrderApprovalStatus orderApprovalStatus
) { }
