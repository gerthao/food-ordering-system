package com.food.ordering.system.order.service.domain;

import com.food.ordering.system.order.service.domain.dto.create.CreateOrderCommand;
import com.food.ordering.system.order.service.domain.dto.create.CreateOrderResponse;
import com.food.ordering.system.order.service.domain.mapper.OrderDataMapper;
import com.food.ordering.system.order.service.domain.ports.output.message.publishing.payment.OrderCreatedPaymentRequestMessagePublisher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;

@Slf4j
@Component
public class OrderCreateCommandHandler {
    private final OrderDataMapper orderDataMapper;
    private final OrderCreateHelper orderCreateHelper;
    private final OrderCreatedPaymentRequestMessagePublisher publisher;

    public OrderCreateCommandHandler(OrderDataMapper orderDataMapper, OrderCreateHelper orderCreateHelper, OrderCreatedPaymentRequestMessagePublisher publisher) {
        this.orderDataMapper   = orderDataMapper;
        this.orderCreateHelper = orderCreateHelper;
        this.publisher         = publisher;
    }

    public CreateOrderResponse createOrder(CreateOrderCommand createOrderCommand) {
        var orderCreatedEvent = orderCreateHelper.persistOrder(createOrderCommand);
        log.info(MessageFormat.format("Order is created with id: {0}", orderCreatedEvent.getOrder().getId().getValue()));
        publisher.publish(orderCreatedEvent);

        return orderDataMapper.orderToCreateOrderResponse(
                orderCreatedEvent.getOrder(), "Order created successfully."
        );
    }
}
