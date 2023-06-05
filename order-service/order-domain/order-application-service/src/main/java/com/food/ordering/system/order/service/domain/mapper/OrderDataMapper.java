package com.food.ordering.system.order.service.domain.mapper;

import com.food.ordering.system.order.service.domain.dto.create.CreateOrderCommand;
import com.food.ordering.system.order.service.domain.dto.create.CreateOrderResponse;
import com.food.ordering.system.order.service.domain.dto.create.OrderAddress;
import com.food.ordering.system.order.service.domain.dto.create.OrderItem;
import com.food.ordering.system.order.service.domain.entity.Order;
import com.food.ordering.system.order.service.domain.entity.Product;
import com.food.ordering.system.order.service.domain.entity.Restaurant;
import com.food.ordering.system.order.service.domain.valueobject.*;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class OrderDataMapper {
    public Restaurant createOrderCommandToRestaurant(CreateOrderCommand createOrderCommand) {
        return Restaurant.builder()
                .withId(new RestaurantId(createOrderCommand.restaurantId()))
                .withProducts(
                        createOrderCommand.items().stream()
                                .map(OrderItem::productId)
                                .map(ProductId::new)
                                .map(Product::new)
                                .collect(Collectors.toList())
                )
                .build();
    }

    public Order createOrderCommandToOrder(CreateOrderCommand createOrderCommand) {
        return Order.builder()
                .withCustomerId(new CustomerId(createOrderCommand.customerId()))
                .withRestaurantId(new RestaurantId(createOrderCommand.restaurantId()))
                .withDeliveryAddress(orderAddressToStreetAddress(createOrderCommand.address()))
                .withPrice(new Money(createOrderCommand.price()))
                .withItems(orderItemsToOrderItemEntities(createOrderCommand.items()))
                .build();

    }

    private List<com.food.ordering.system.order.service.domain.entity.OrderItem> orderItemsToOrderItemEntities(List<OrderItem> items) {
        return items.stream()
                .map(item -> com.food.ordering.system.order.service.domain.entity.OrderItem.builder()
                        .withProduct(new Product(new ProductId(item.productId())))
                        .withQuantity(item.quantity())
                        .withSubtotal(new Money(item.subtotal()))
                        .build())
                .collect(Collectors.toList());
    }

    private StreetAddress orderAddressToStreetAddress(OrderAddress address) {
        return new StreetAddress(UUID.randomUUID(), address.street(), address.postalCode(), address.city());
    }

    public CreateOrderResponse orderToCreateOrderResponse(Order order) {
        return CreateOrderResponse.builder()
                .orderTrackingId(order.getTrackingId().getValue())
                .orderStatus(order.getOrderStatus())
                .build();
    }
}
