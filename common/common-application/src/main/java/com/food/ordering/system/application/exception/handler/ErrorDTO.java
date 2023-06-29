package com.food.ordering.system.application.exception.handler;

import lombok.Builder;

@Builder
public record ErrorDTO(String code, String message) {

}
