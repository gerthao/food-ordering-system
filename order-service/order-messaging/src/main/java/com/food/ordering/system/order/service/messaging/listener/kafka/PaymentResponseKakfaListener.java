package com.food.ordering.system.order.service.messaging.listener.kafka;

import com.food.ordering.system.kafka.consumer.service.KafkaConsumer;
import com.food.ordering.system.kafka.order.avro.model.PaymentResponseAvroModel;
import com.food.ordering.system.order.service.domain.ports.input.message.listener.payment.PaymentResponseMessageListener;
import com.food.ordering.system.order.service.messaging.mapper.OrderMessagingDataMapper;

import java.util.List;

public class PaymentResponseKakfaListener implements KafkaConsumer<PaymentResponseAvroModel> {
    private final PaymentResponseMessageListener listener;
    private final OrderMessagingDataMapper mapper;

    public PaymentResponseKakfaListener(PaymentResponseMessageListener listener, OrderMessagingDataMapper mapper) {
        this.listener = listener;
        this.mapper = mapper;
    }

    @Override
    public void receive(List<PaymentResponseAvroModel> messages, List<Long> keys, List<Integer> partitions, List<Long> offsets) {

    }
}
