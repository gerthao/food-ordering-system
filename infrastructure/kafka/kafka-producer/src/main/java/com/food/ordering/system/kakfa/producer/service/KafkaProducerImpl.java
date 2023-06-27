package com.food.ordering.system.kakfa.producer.service;

import com.food.ordering.system.kakfa.producer.exception.KafkaProducerException;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.function.BiConsumer;

@Slf4j
@Component
public class KafkaProducerImpl<K extends Serializable, V extends SpecificRecordBase> implements KafkaProducer<K, V> {

    private final KafkaTemplate<K, V> kafkaTemplate;

    public KafkaProducerImpl(KafkaTemplate<K, V> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void send(String topicName, K key, V message, BiConsumer<SendResult<K, V>, Throwable> callback) throws KafkaProducerException {
        log.info("Sending message={} to topic={}", message, topicName);

        kafkaTemplate
                .send(topicName, key, message)
                .whenComplete(callback)
                .exceptionally(e -> {
                    log.error("Error on kafka producer with key: {}, message: {}, and exception: {}", key, message, e.getMessage());
                    throw new KafkaProducerException(MessageFormat.format("Error on kafka producer with key: {0} and message: {1}", key, message));
                });
    }

    @PreDestroy
    void close() {
        if (kafkaTemplate != null) {
            log.info("Closing kafka producer.");
            kafkaTemplate.destroy();
        }
    }
}
