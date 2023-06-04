package com.food.ordering.system.order.service.domain.dto.track;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.ToString;

import java.util.UUID;

@Builder
@AllArgsConstructor
@ToString
public record TrackOrderQuery(
        @NotNull UUID orderTrackingId
) { }
