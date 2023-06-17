package com.food.ordering.system.order.service.messaging.listener.kafka;

import com.food.ordering.system.kafka.consumer.service.KafkaConsumer;
import com.food.ordering.system.kafka.order.avro.model.PaymentResponseAvroModel;

import java.util.List;

public class PaymentResponseKakfaListener implements KafkaConsumer<PaymentResponseAvroModel> {
    @Override
    public void receive(List<PaymentResponseAvroModel> messages, List<Long> keys, List<Integer> partitions, List<Long> offsets) {
        
    }
}
