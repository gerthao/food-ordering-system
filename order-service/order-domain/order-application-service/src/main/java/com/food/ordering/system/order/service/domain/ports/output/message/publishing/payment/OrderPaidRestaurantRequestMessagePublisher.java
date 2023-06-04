package com.food.ordering.system.order.service.domain.ports.output.message.publishing.payment;

import com.food.ordering.system.order.service.domain.event.DomainEvent;
import com.food.ordering.system.order.service.domain.event.OrderPaidEvent;

public interface OrderPaidRestaurantRequestMessagePublisher extends DomainEvent<OrderPaidEvent> { }
