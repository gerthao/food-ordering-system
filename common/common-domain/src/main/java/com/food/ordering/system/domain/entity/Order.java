package com.food.ordering.system.domain.entity;

import com.food.ordering.system.domain.valueobject.*;

import java.util.List;

public class Order extends AggregateRoot<OrderId> {
    private final CustomerId      customerId;
    private final RestaurantId    restaurantId;
    private final StreetAddress   deliveryAddress;
    private final Money           price;
    private final List<OrderItem> items;

    private TrackingId   trackingId;
    private OrderStatus  orderStatus;
    private List<String> failureMessages;

    private Order(Builder builder) {
        super.setId(builder.orderId);
        customerId      = builder.customerId;
        restaurantId    = builder.restaurantId;
        deliveryAddress = builder.deliveryAddress;
        price           = builder.price;
        items           = builder.items;
        trackingId      = builder.trackingId;
        orderStatus     = builder.orderStatus;
        failureMessages = builder.failureMessages;
    }

    public static Builder builder() {
        return new Builder();
    }

    public CustomerId getCustomerId() {
        return customerId;
    }

    public RestaurantId getRestaurantId() {
        return restaurantId;
    }

    public StreetAddress getDeliveryAddress() {
        return deliveryAddress;
    }

    public Money getPrice() {
        return price;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public TrackingId getTrackingId() {
        return trackingId;
    }

    public OrderStatus getOrderStatus() {
        return orderStatus;
    }

    public List<String> getFailureMessages() {
        return failureMessages;
    }

    public static final class Builder {
        private OrderId         orderId;
        private CustomerId      customerId;
        private RestaurantId    restaurantId;
        private StreetAddress   deliveryAddress;
        private Money           price;
        private List<OrderItem> items;
        private TrackingId      trackingId;
        private OrderStatus     orderStatus;
        private List<String>    failureMessages;

        private Builder() { }

        public Builder withId(OrderId val) {
            orderId = val;
            return this;
        }

        public Builder withCustomerId(CustomerId val) {
            customerId = val;
            return this;
        }

        public Builder withRestaurantId(RestaurantId val) {
            restaurantId = val;
            return this;
        }

        public Builder withDeliveryAddress(StreetAddress val) {
            deliveryAddress = val;
            return this;
        }

        public Builder withPrice(Money val) {
            price = val;
            return this;
        }

        public Builder withItems(List<OrderItem> val) {
            items = val;
            return this;
        }

        public Builder withTrackingId(TrackingId val) {
            trackingId = val;
            return this;
        }

        public Builder withOrderStatus(OrderStatus val) {
            orderStatus = val;
            return this;
        }

        public Builder withFailureMessages(List<String> val) {
            failureMessages = val;
            return this;
        }

        public Order build() {
            return new Order(this);
        }
    }
}
