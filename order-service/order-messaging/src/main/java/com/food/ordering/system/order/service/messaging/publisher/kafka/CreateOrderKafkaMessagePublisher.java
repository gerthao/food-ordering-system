package com.food.ordering.system.order.service.messaging.publisher.kafka;

import com.food.ordering.system.kafka.order.avro.model.PaymentRequestAvroModel;
import com.food.ordering.system.order.service.domain.config.OrderServiceConfigData;
import com.food.ordering.system.order.service.domain.event.OrderCreatedEvent;
import com.food.ordering.system.order.service.domain.ports.output.message.publishing.payment.OrderCreatedPaymentRequestMessagePublisher;
import com.food.ordering.system.order.service.messaging.mapper.OrderMessagingDataMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CreateOrderKafkaMessagePublisher implements OrderCreatedPaymentRequestMessagePublisher {
    private final OrderMessagingDataMapper mapper;
    private final OrderServiceConfigData configData;
    private final KafkaProducer<String, PaymentRequestAvroModel> producer;
    private final OrderKafkaMessageHelper messageHelper;

    public CreateOrderKafkaMessagePublisher(OrderMessagingDataMapper mapper, OrderServiceConfigData configData, KafkaProducer<String, PaymentRequestAvroModel> producer, OrderKafkaMessageHelper messageHelper) {
        this.mapper        = mapper;
        this.configData    = configData;
        this.producer      = producer;
        this.messageHelper = messageHelper;
    }

    @Override
    public void publish(OrderCreatedEvent domainEvent) {
        var orderId = domainEvent.getOrder().getId().getValue();
        log.info("Received OrderCreatedEvent for order id: {}", orderId);

        try {
            var paymentRequestAvroModel = mapper.toPaymentRequestAvroModel(domainEvent);

            producer.send(
                    new ProducerRecord<>(configData.getPaymentRequestTopicName(), orderId.toString(), paymentRequestAvroModel),
                    messageHelper.getKafkaCallback(configData.getPaymentRequestTopicName(), orderId.toString(), "PaymentRequestAvroModel", paymentRequestAvroModel)
            );
            log.info("PaymentRequestAvroModel sent to Kafka for order id: {}", paymentRequestAvroModel.getOrderId());
        } catch (Exception e) {
            log.error("Error while sending PaymentRequestAvroModel message to kakfa with order id: {}, error: {}", orderId, e.getMessage());
        }

    }
}
