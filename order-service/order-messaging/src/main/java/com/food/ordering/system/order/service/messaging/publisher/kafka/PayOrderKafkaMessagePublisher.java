package com.food.ordering.system.order.service.messaging.publisher.kafka;

import com.food.ordering.system.kafka.order.avro.model.RestaurantApprovalRequestAvroModel;
import com.food.ordering.system.kakfa.producer.service.KafkaProducer;
import com.food.ordering.system.order.service.domain.config.OrderServiceConfigData;
import com.food.ordering.system.order.service.domain.event.OrderPaidEvent;
import com.food.ordering.system.order.service.domain.ports.output.message.publishing.payment.OrderPaidRestaurantRequestMessagePublisher;
import com.food.ordering.system.order.service.messaging.mapper.OrderMessagingDataMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PayOrderKafkaMessagePublisher implements OrderPaidRestaurantRequestMessagePublisher {
    private final OrderMessagingDataMapper mapper;
    private final OrderServiceConfigData configData;
    private final KafkaProducer<String, RestaurantApprovalRequestAvroModel> producer;
    private final OrderKafkaMessageHelper messageHelper;

    public PayOrderKafkaMessagePublisher(OrderMessagingDataMapper mapper, OrderServiceConfigData configData, KafkaProducer<String, RestaurantApprovalRequestAvroModel> producer, OrderKafkaMessageHelper messageHelper) {
        this.mapper = mapper;
        this.configData = configData;
        this.producer = producer;
        this.messageHelper = messageHelper;
    }

    @Override
    public void publish(OrderPaidEvent domainEvent) {
        var orderId = domainEvent.getOrder().getId();
        var restaurantApprovalRequestAvroModel = mapper.toPaymentRequestAvroModel(domainEvent);

        try {
            producer.send(
                    configData.getPaymentRequestTopicName(), orderId.toString(), restaurantApprovalRequestAvroModel,
                    messageHelper.getKafkaBiConsumerFunction(configData.getPaymentRequestTopicName(), orderId.toString(), "RestaurantApprovalRequestAvroModel", restaurantApprovalRequestAvroModel)
            );
            log.info("RestaurantApprovalRequestAvroModel sent to Kafka for order id: {}", restaurantApprovalRequestAvroModel.getOrderId());
        } catch (Exception e) {
            log.error("Error while sending RestaurantApprovalRequestAvroModel message to kakfa with order id: {}, error: {}", orderId, e.getMessage());
        }
    }
}
