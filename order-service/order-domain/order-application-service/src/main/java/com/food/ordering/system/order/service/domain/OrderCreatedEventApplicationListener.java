package com.food.ordering.system.order.service.domain;

import com.food.ordering.system.order.service.domain.event.OrderCreatedEvent;
import com.food.ordering.system.order.service.domain.ports.output.message.publishing.payment.OrderCreatedPaymentRequestMessagePublisher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
public class OrderCreatedEventApplicationListener {
    private final OrderCreatedPaymentRequestMessagePublisher messagePublisher;


    public OrderCreatedEventApplicationListener(OrderCreatedPaymentRequestMessagePublisher messagePublisher) { this.messagePublisher = messagePublisher; }

    @TransactionalEventListener
    void process(OrderCreatedEvent event) {

    }
}
