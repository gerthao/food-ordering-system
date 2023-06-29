package com.food.ordering.system.order.service.messaging.listener.kafka;

import com.food.ordering.system.kafka.consumer.service.KafkaConsumer;
import com.food.ordering.system.kafka.order.avro.model.PaymentResponseAvroModel;
import com.food.ordering.system.order.service.domain.ports.input.message.listener.payment.PaymentResponseMessageListener;
import com.food.ordering.system.order.service.messaging.mapper.OrderMessagingDataMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class PaymentResponseKakfaListener implements KafkaConsumer<PaymentResponseAvroModel> {
    private final PaymentResponseMessageListener listener;
    private final OrderMessagingDataMapper mapper;

    public PaymentResponseKakfaListener(PaymentResponseMessageListener listener, OrderMessagingDataMapper mapper) {
        this.listener = listener;
        this.mapper   = mapper;
    }

    @Override
    @KafkaListener(id = "${kafka-consumer-config.payment-consumer-group-id}", topics = "${order-service.payment-response-topic-name}")
    public void receive(
            @Payload List<PaymentResponseAvroModel> messages,
            @Header(KafkaHeaders.RECEIVED_KEY) List<String> keys,
            @Header(KafkaHeaders.RECEIVED_PARTITION) List<Integer> partitions,
            @Header(KafkaHeaders.OFFSET) List<Long> offsets) {
        log.info(
                "{} number of payment responses with keys: {}, partitions: {}, and offsets: {}",
                messages.size(),
                keys.toString(),
                partitions.toString(),
                offsets.toString()
        );

        messages.forEach(this::handleMessage);
    }

    private void handleMessage(PaymentResponseAvroModel p) {
        var orderId = p.getOrderId();
        var status  = p.getPaymentStatus();

        switch (status) {
            case COMPLETED -> {
                log.info("Processing successful payment for order id: {}", orderId);
                listener.paymentCompleted(mapper.toPaymentResponse(p));
            }
            case CANCELLED, FAILED -> {
                log.info("Processing unsuccessful payment for order id: {}", orderId);
                listener.paymentCancelled(mapper.toPaymentResponse(p));
            }
        }
    }
}
