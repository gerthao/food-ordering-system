package com.food.ordering.system.order.service.domain.dto.track;

import com.food.ordering.system.order.service.domain.valueobject.OrderStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.ToString;

import java.util.List;
import java.util.UUID;

@Builder
@AllArgsConstructor
@ToString
public record TrackOrderResponse(
        @NotNull UUID orderTrackingId,
        @NotNull OrderStatus orderStatus,
        List<String> failureMessages
) { }
