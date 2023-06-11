package com.food.ordering.system.order.service.dataaccess.order.mapper;

import com.food.ordering.system.order.service.dataaccess.order.entity.OrderAddressEntity;
import com.food.ordering.system.order.service.dataaccess.order.entity.OrderEntity;
import com.food.ordering.system.order.service.dataaccess.order.entity.OrderItemEntity;
import com.food.ordering.system.order.service.domain.entity.Order;
import com.food.ordering.system.order.service.domain.entity.OrderItem;
import com.food.ordering.system.order.service.domain.entity.Product;
import com.food.ordering.system.order.service.domain.valueobject.*;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class OrderDataAccessMapper {

    public static final String FAILURE_MESSAGE_DELIMITER = ",";

    public OrderEntity toOrderEntity(Order order) {
        var orderEntity = OrderEntity.builder()
                .id(order.getId().getValue())
                .customerId(order.getCustomerId().getValue())
                .restaurantId(order.getRestaurantId().getValue())
                .trackingId(order.getTrackingId().getValue())
                .address(toAddressEntity(order.getDeliveryAddress()))
                .price(order.getPrice().amount())
                .items(toOrderItemEntities(order.getItems()))
                .orderStatus(order.getOrderStatus())
                .failureMessage(formatFailureMessageFromOrder(order))
                .build();

        orderEntity.getAddress().setOrder(orderEntity);
        orderEntity.getItems().forEach(itemEntity -> itemEntity.setOrder(orderEntity));
 
        return orderEntity;
    }

    public Order toOrder(OrderEntity orderEntity) {
        return Order.builder()
                .withId(new OrderId(orderEntity.getId()))
                .withCustomerId(new CustomerId(orderEntity.getCustomerId()))
                .withRestaurantId(new RestaurantId(orderEntity.getRestaurantId()))
                .withTrackingId(new TrackingId(orderEntity.getTrackingId()))
                .withDeliveryAddress(toDeliveryAddress(orderEntity.getAddress()))
                .withPrice(new Money(orderEntity.getPrice()))
                .withItems(toOrderItems(orderEntity.getItems()))
                .withOrderStatus(orderEntity.getOrderStatus())
                .withFailureMessages(
                        orderEntity.getFailureMessage() == null || orderEntity.getFailureMessage().isEmpty() ?
                                new ArrayList<>() :
                                new ArrayList<>(Arrays.asList(orderEntity.getFailureMessage().split(FAILURE_MESSAGE_DELIMITER))))
                .build();
    }

    private List<OrderItem> toOrderItems(List<OrderItemEntity> items) {
        return items.stream().map(itemEntity ->
                OrderItem.builder()
                        .withId(new OrderItemId(itemEntity.getId()))
                        .withProduct(new Product(new ProductId(itemEntity.getProductId())))
                        .withPrice(new Money(itemEntity.getPrice()))
                        .withQuantity(itemEntity.getQuantity())
                        .withSubtotal(new Money(itemEntity.getSubtotal()))
                        .build()
        ).collect(Collectors.toList());
    }

    private StreetAddress toDeliveryAddress(OrderAddressEntity address) {
        return StreetAddress.builder()
                .id(address.getId())
                .street(address.getStreet())
                .postalCode(address.getPostalCode())
                .city(address.getCity())
                .postalCode(address.getPostalCode())
                .build();
    }

    private String formatFailureMessageFromOrder(Order order) {
        return order.getFailureMessages() == null ? "" : String.join(FAILURE_MESSAGE_DELIMITER, order.getFailureMessages());
    }

    private List<OrderItemEntity> toOrderItemEntities(List<OrderItem> items) {
        return items.stream().map(orderItem ->
                OrderItemEntity.builder()
                        .id(orderItem.getId().getValue())
                        .productId(orderItem.getProduct().getId().getValue())
                        .price(orderItem.getPrice().amount())
                        .quantity(orderItem.getQuantity())
                        .build()
        ).collect(Collectors.toList());
    }

    private OrderAddressEntity toAddressEntity(StreetAddress deliveryAddress) {
        return OrderAddressEntity.builder()
                .id(deliveryAddress.id())
                .street(deliveryAddress.street())
                .postalCode(deliveryAddress.postalCode())
                .city(deliveryAddress.city())
                .build();
    }
}
