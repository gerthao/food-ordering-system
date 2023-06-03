package com.food.ordering.system.order.service.domain.entity;

import com.food.ordering.system.order.service.domain.exception.OrderDomainException;
import com.food.ordering.system.order.service.domain.valueobject.*;

import java.text.MessageFormat;
import java.util.List;
import java.util.UUID;

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

    public void pay() throws OrderDomainException {
        if (orderStatus != OrderStatus.PENDING)
            throw new OrderDomainException("Order is not in correct state for pay operation!");
        orderStatus = OrderStatus.PAID;
    }

    public void approve() throws OrderDomainException {
        if (orderStatus != OrderStatus.PAID)
            throw new OrderDomainException("Order is not in correct state for approve operation!");
        orderStatus = OrderStatus.APPROVED;
    }

    public void initCancel(List<String> failureMessages) throws OrderDomainException {
        if (orderStatus != OrderStatus.PAID)
            throw new OrderDomainException("Order is not in correct state for initCancel operation!");
        orderStatus = OrderStatus.CANCELLING;
        updateFailureMessages(failureMessages);
    }

    public void cancel(List<String> failureMessages) throws OrderDomainException {
        if (!(orderStatus == OrderStatus.CANCELLING || orderStatus == OrderStatus.PENDING))
            throw new OrderDomainException("Order is not in correct state for cancel operation!");
        orderStatus = OrderStatus.CANCELLED;
        updateFailureMessages(failureMessages);
    }

    private void updateFailureMessages(List<String> failureMessages) {
        if (this.failureMessages != null && failureMessages != null)
            this.failureMessages.addAll(failureMessages.stream().filter(m -> !m.isEmpty() && !m.isBlank()).toList());

        if (this.failureMessages == null) this.failureMessages = failureMessages;
    }

    public void initializeOrder() {
        setId(new OrderId(UUID.randomUUID()));
        trackingId  = new TrackingId(UUID.randomUUID());
        orderStatus = OrderStatus.PENDING;
        initializeOrderItems();
    }

    public void validateOrder() {
        validateInitialOrder();
        validateTotalPrice();
        validateItemsPrice();
    }

    private void validateInitialOrder() throws OrderDomainException {
        if (orderStatus != null || getId() != null)
            throw new OrderDomainException("Order is not in correct state for initialization.");
    }

    private void validateTotalPrice() throws OrderDomainException {
        if (price == null || !price.isGreaterThanZero())
            throw new OrderDomainException("Total price must be greater than zero.");
    }

    private void validateItemsPrice() throws OrderDomainException {
        var orderItemsTotal = items.parallelStream().map(item -> {
            validateItemPrice(item);
            return item.getSubtotal();
        }).reduce(Money.ZERO, Money::add);

        if (!price.equals(orderItemsTotal))
            throw new OrderDomainException(
                    MessageFormat.format(
                            "Total price: {0} is not equal to order items total: {1}",
                            price.getAmount(),
                            orderItemsTotal.getAmount()
                    )
            );
    }

    private void validateItemPrice(OrderItem item) throws OrderDomainException {
        if (!item.isPriceValid())
            throw new OrderDomainException(
                    MessageFormat.format(
                            "Order item price: {0} is not valid for product {1}",
                            item.getPrice().getAmount(),
                            item.getProduct().getId().getValue()
                    )
            );
    }


    private void initializeOrderItems() {
        long itemId = 1;
        for (OrderItem orderItem : items) {
            orderItem.initializeOrderItem(super.getId(), new OrderItemId(itemId++));
        }
    }

    public CustomerId getCustomerId() { return customerId; }

    public RestaurantId getRestaurantId() { return restaurantId; }

    public StreetAddress getDeliveryAddress() { return deliveryAddress; }

    public Money getPrice() { return price; }

    public List<OrderItem> getItems() { return items; }

    public TrackingId getTrackingId() { return trackingId; }

    public OrderStatus getOrderStatus() { return orderStatus; }

    public List<String> getFailureMessages() { return failureMessages; }

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

        public Order build() { return new Order(this); }
    }
}
