package com.food.ordering.system.order.service.messaging.mapper;

import com.food.ordering.system.kafka.order.avro.model.*;
import com.food.ordering.system.order.service.domain.dto.message.PaymentResponse;
import com.food.ordering.system.order.service.domain.dto.message.RestaurantApprovalResponse;
import com.food.ordering.system.order.service.domain.entity.Order;
import com.food.ordering.system.order.service.domain.event.OrderCancelledEvent;
import com.food.ordering.system.order.service.domain.event.OrderCreatedEvent;
import com.food.ordering.system.order.service.domain.event.OrderPaidEvent;
import com.food.ordering.system.order.service.domain.valueobject.OrderApprovalStatus;
import com.food.ordering.system.order.service.domain.valueobject.PaymentStatus;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class OrderMessagingDataMapper {

    public PaymentRequestAvroModel toPaymentRequestAvroModel(OrderCreatedEvent orderCreatedEvent) {
        var order = orderCreatedEvent.getOrder();

        return PaymentRequestAvroModel.newBuilder()
                .setId(UUID.randomUUID())
                .setSagaId(UUID.randomUUID())
                .setCustomerId(order.getCustomerId().getValue())
                .setOrderId(order.getId().getValue())
                .setPrice(order.getPrice().amount())
                .setCreatedAt(orderCreatedEvent.getCreatedAt().toInstant())
                .setPaymentOrderStatus(PaymentOrderStatus.PENDING)
                .build();
    }

    public PaymentRequestAvroModel toPaymentRequestAvroModel(OrderCancelledEvent orderCancelledEvent) {
        var order = orderCancelledEvent.getOrder();

        return PaymentRequestAvroModel.newBuilder()
                .setId(UUID.randomUUID())
                .setSagaId(UUID.randomUUID())
                .setCustomerId(order.getCustomerId().getValue())
                .setOrderId(order.getId().getValue())
                .setPrice(order.getPrice().amount())
                .setCreatedAt(orderCancelledEvent.getCreatedAt().toInstant())
                .setPaymentOrderStatus(PaymentOrderStatus.CANCELLED)
                .build();
    }

    public RestaurantApprovalRequestAvroModel toPaymentRequestAvroModel(OrderPaidEvent orderPaidEvent) {
        var order = orderPaidEvent.getOrder();

        return RestaurantApprovalRequestAvroModel.newBuilder()
                .setId(UUID.randomUUID())
                .setSagaId(UUID.randomUUID())
                .setOrderId(order.getId().getValue())
                .setRestaurantId(order.getRestaurantId().getValue())
                .setRestaurantOrderStatus(com.food.ordering.system.kafka.order.avro.model.RestaurantOrderStatus.valueOf(order.getOrderStatus().name()))
                .setProducts(getProducts(order))
                .setPrice(order.getPrice().amount())
                .setCreatedAt(orderPaidEvent.getCreatedAt().toInstant())
                .setRestaurantOrderStatus(RestaurantOrderStatus.PAID)
                .build();
    }

    public PaymentResponse toPaymentResponse(PaymentResponseAvroModel model) {
        return PaymentResponse.builder()
                .id(model.getId().toString())
                .sagaId(model.getSagaId().toString())
                .paymentId(model.getPaymentId().toString())
                .orderId(model.getOrderId().toString())
                .price(model.getPrice())
                .createdAt(model.getCreatedAt())
                .paymentStatus(PaymentStatus.valueOf(model.getPaymentStatus().name()))
                .failureMessages(model.getFailureMessages())
                .build();
    }

    private List<Product> getProducts(Order order) {
        return order.getItems().stream().map(item ->
                Product.newBuilder()
                        .setId(item.getProduct().getId().getValue().toString())
                        .setQuantity(item.getQuantity())
                        .build()
        ).collect(Collectors.toList());
    }

    public RestaurantApprovalResponse toRestaurantApprovalResponse(RestaurantApprovalResponseAvroModel r) {
        return RestaurantApprovalResponse.builder()
                .id(r.getId().toString())
                .sagaId(r.getId().toString())
                .restaurantId(r.getRestaurantId().toString())
                .orderId(r.getOrderId().toString())
                .createdAt(r.getCreatedAt())
                .orderApprovalStatus(OrderApprovalStatus.valueOf(r.getOrderApprovalStatus().name()))
                .build();
    }
}
