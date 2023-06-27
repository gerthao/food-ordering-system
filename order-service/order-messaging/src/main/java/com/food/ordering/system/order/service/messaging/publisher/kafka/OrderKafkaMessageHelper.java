package com.food.ordering.system.order.service.messaging.publisher.kafka;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.Callback;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.function.BiConsumer;

@Slf4j
@Component
public class OrderKafkaMessageHelper {
    public <T> Callback getKafkaCallback(String topicName, String orderId, String requestAvroModelName, T requestAvroModel) {
        return (recordMetadata, e) -> {
            if (e != null) {
                log.error(
                        "Error while sending {} message: {} to topic: {}",
                        requestAvroModelName,
                        requestAvroModel.toString(),
                        topicName
                );
            } else {
                log.info(
                        "Received successful response from Kafka for order id: {}, topic: {}, partition: {}, offset: {}, timestamp: {}",
                        orderId,
                        recordMetadata.topic(),
                        recordMetadata.partition(),
                        recordMetadata.offset(),
                        recordMetadata.timestamp()
                );
            }
        };
    }

    public <T, K, V> BiConsumer<SendResult<K, V>, Throwable> getKafkaBiConsumerFunction(String topicName, String orderId, String requestAvroModelName, T requestAvroModel) {
        return (sendResult, exception) -> {
            if (exception != null) {
                log.error(
                        "Error while sending {} message: {} to topic: {}",
                        requestAvroModelName,
                        requestAvroModel.toString(),
                        topicName
                );
            } else {
                var recordMetadata = sendResult.getRecordMetadata();
                log.info(
                        "Received successful response from Kafka for order id: {}, topic: {}, partition: {}, offset: {}, timestamp: {}",
                        orderId,
                        recordMetadata.topic(),
                        recordMetadata.partition(),
                        recordMetadata.offset(),
                        recordMetadata.timestamp()
                );
            }
        };
    }
}
