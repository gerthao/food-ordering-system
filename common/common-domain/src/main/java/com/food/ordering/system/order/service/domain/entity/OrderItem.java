package com.food.ordering.system.order.service.domain.entity;

import com.food.ordering.system.order.service.domain.valueobject.Money;
import com.food.ordering.system.order.service.domain.valueobject.OrderId;
import com.food.ordering.system.order.service.domain.valueobject.OrderItemId;

public class OrderItem extends BaseEntity<OrderItemId> {
    private final Product product;
    private final Money   price;
    private final Money   subtotal;
    private final int     quantity;
    private       OrderId orderId;

    private OrderItem(Builder builder) {
        super.setId(builder.orderItemId);
        product  = builder.product;
        price    = builder.price;
        subtotal = builder.subtotal;
        quantity = builder.quantity;
    }

    public static Builder builder() {
        return new Builder();
    }

    public Product getProduct() { return product; }

    public Money getPrice() { return price; }

    public Money getSubtotal() { return subtotal; }

    public int getQuantity() { return quantity; }

    public OrderId getOrderId() { return orderId; }

    public void initializeOrderItem(OrderId orderId, OrderItemId orderItemId) {
        this.orderId = orderId;
        super.setId(orderItemId);
    }

    public boolean isPriceValid() {
        return price.isGreaterThanZero() && price.equals(product.getPrice()) && price.multiply(quantity).equals(subtotal);
    }

    public static final class Builder {
        private OrderItemId orderItemId;
        private Product     product;
        private Money       price;
        private Money       subtotal;
        private int         quantity;

        private Builder() { }

        public Builder withId(OrderItemId val) {
            orderItemId = val;
            return this;
        }

        public Builder withProduct(Product val) {
            product = val;
            return this;
        }

        public Builder withPrice(Money val) {
            price = val;
            return this;
        }

        public Builder withSubtotal(Money val) {
            subtotal = val;
            return this;
        }

        public Builder withQuantity(int val) {
            quantity = val;
            return this;
        }

        public OrderItem build() {
            return new OrderItem(this);
        }
    }
}
