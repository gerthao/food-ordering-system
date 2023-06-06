package com.food.ordering.system.order.service.domain;

import com.food.ordering.system.order.service.domain.dto.create.CreateOrderCommand;
import com.food.ordering.system.order.service.domain.dto.create.CreateOrderResponse;
import com.food.ordering.system.order.service.domain.dto.create.OrderAddress;
import com.food.ordering.system.order.service.domain.dto.create.OrderItem;
import com.food.ordering.system.order.service.domain.entity.Customer;
import com.food.ordering.system.order.service.domain.entity.Order;
import com.food.ordering.system.order.service.domain.entity.Product;
import com.food.ordering.system.order.service.domain.entity.Restaurant;
import com.food.ordering.system.order.service.domain.exception.OrderDomainException;
import com.food.ordering.system.order.service.domain.mapper.OrderDataMapper;
import com.food.ordering.system.order.service.domain.ports.input.service.OrderApplicationService;
import com.food.ordering.system.order.service.domain.ports.output.repository.CustomerRepository;
import com.food.ordering.system.order.service.domain.ports.output.repository.OrderRepository;
import com.food.ordering.system.order.service.domain.ports.output.repository.RestaurantRepository;
import com.food.ordering.system.order.service.domain.valueobject.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(classes = OrderTestConfiguration.class)
public class OrderApplicationServiceTest {

    private final UUID       CUSTOMER_ID   = UUID.fromString("b1d498c3-7df5-42a1-be43-4783c6fc6320");
    private final UUID       RESTAURANT_ID = UUID.fromString("d44edde2-b085-43d6-b495-f029f2fef837");
    private final UUID       PRODUCT_ID    = UUID.fromString("0281d1d7-86ae-4111-863f-2d910a8ec36e");
    private final UUID       ORDER_ID      = UUID.fromString("be8d8e18-be48-41c0-bf82-1ba5852a986f");
    private final BigDecimal PRICE         = new BigDecimal("200.00");

    @Autowired
    private OrderApplicationService orderApplicationService;
    @Autowired
    private OrderDataMapper         orderDataMapper;
    @Autowired
    private OrderRepository         orderRepository;
    @Autowired
    private CustomerRepository      customerRepository;
    @Autowired
    private RestaurantRepository    restaurantRepository;
    private CreateOrderCommand      createOrderCommand;
    private CreateOrderCommand      createOrderCommandWrongPrice;
    private CreateOrderCommand      createOrderCommandWrongProductPrice;

    @BeforeAll
    public void init() {
        createOrderCommand = CreateOrderCommand.builder()
                .customerId(CUSTOMER_ID)
                .restaurantId(RESTAURANT_ID)
                .address(
                        OrderAddress.builder()
                                .street("street_1")
                                .postalCode("1000AB")
                                .city("Paris")
                                .build()
                ).price(PRICE)
                .items(
                        List.of(
                                OrderItem.builder()
                                        .productId(PRODUCT_ID)
                                        .quantity(1)
                                        .price(new BigDecimal("50.00"))
                                        .subtotal(new BigDecimal("50.00"))
                                        .build(),
                                OrderItem.builder()
                                        .productId(PRODUCT_ID)
                                        .quantity(3)
                                        .price(new BigDecimal("50.00"))
                                        .subtotal(new BigDecimal("150.00"))
                                        .build()
                        )
                ).build();

        createOrderCommandWrongPrice = CreateOrderCommand.builder()
                .customerId(CUSTOMER_ID)
                .restaurantId(RESTAURANT_ID)
                .address(
                        OrderAddress.builder()
                                .street("street_1")
                                .postalCode("1000AB")
                                .city("Paris")
                                .build()
                ).price(new BigDecimal("250.00"))
                .items(
                        List.of(
                                OrderItem.builder()
                                        .productId(PRODUCT_ID)
                                        .quantity(1)
                                        .price(new BigDecimal("50.00"))
                                        .subtotal(new BigDecimal("50.00"))
                                        .build(),
                                OrderItem.builder()
                                        .productId(PRODUCT_ID)
                                        .quantity(3)
                                        .price(new BigDecimal("50.00"))
                                        .subtotal(new BigDecimal("150.00"))
                                        .build()
                        )
                ).build();

        createOrderCommandWrongProductPrice = CreateOrderCommand.builder()
                .customerId(CUSTOMER_ID)
                .restaurantId(RESTAURANT_ID)
                .address(
                        OrderAddress.builder()
                                .street("street_1")
                                .postalCode("1000AB")
                                .city("Paris")
                                .build()
                ).price(new BigDecimal("210.00"))
                .items(
                        List.of(
                                OrderItem.builder()
                                        .productId(PRODUCT_ID)
                                        .quantity(1)
                                        .price(new BigDecimal("60.00"))
                                        .subtotal(new BigDecimal("60.00"))
                                        .build(),
                                OrderItem.builder()
                                        .productId(PRODUCT_ID)
                                        .quantity(3)
                                        .price(new BigDecimal("50.00"))
                                        .subtotal(new BigDecimal("150.00"))
                                        .build()
                        )
                ).build();

        var customer = new Customer();
        customer.setId(new CustomerId(CUSTOMER_ID));

        var restaurantResponse = Restaurant.builder()
                .withId(new RestaurantId(createOrderCommand.restaurantId()))
                .withProducts(List.of(
                        new Product(new ProductId(PRODUCT_ID), "product-1", new Money(new BigDecimal("50.00"))),
                        new Product(new ProductId(PRODUCT_ID), "product-2", new Money(new BigDecimal("50.00")))
                ))
                .withActive(true)
                .build();

        var order = orderDataMapper.createOrderCommandToOrder(createOrderCommand);
        order.setId(new OrderId(ORDER_ID));

        when(customerRepository.findCustomer(CUSTOMER_ID)).thenReturn(Optional.of(customer));
        when(restaurantRepository.findRestaurantInformation(orderDataMapper.createOrderCommandToRestaurant(createOrderCommand))).thenReturn(Optional.of(restaurantResponse));
        when(orderRepository.save(any(Order.class))).thenReturn(order);
    }

    @Test
    void testCreateOrder() {
        CreateOrderResponse createOrderResponse = orderApplicationService.createOrder(createOrderCommand);
        assertEquals(OrderStatus.PENDING, createOrderResponse.orderStatus());
        assertEquals("Order created successfully.", createOrderResponse.message());
        assertNotNull(createOrderResponse.orderTrackingId());
    }

    @Test
    void testCreateOrderWithWrongTotalPrice() {
        var orderDomainException = assertThrows(OrderDomainException.class, () -> orderApplicationService.createOrder(createOrderCommandWrongPrice));
        assertEquals("Total price: 250 is not equal to order items total: 200", orderDomainException.getMessage());
    }

    @Test
    void testCreateOrderWithWrongProductPrice() {
        var orderDomainException = assertThrows(OrderDomainException.class, () -> orderApplicationService.createOrder(createOrderCommandWrongProductPrice));
        assertEquals("Order item price: 60 is not valid for product " + PRODUCT_ID, orderDomainException.getMessage());
    }

    @Test
    void testCreateOrderWithInactiveRestaurant() {
        var restaurantResponse = Restaurant.builder()
                .withId(new RestaurantId(createOrderCommand.restaurantId()))
                .withProducts(List.of(
                        new Product(new ProductId(PRODUCT_ID), "product-1", new Money(new BigDecimal("50.00"))),
                        new Product(new ProductId(PRODUCT_ID), "product-2", new Money(new BigDecimal("50.00")))
                ))
                .withActive(false)
                .build();

        when(restaurantRepository.findRestaurantInformation(orderDataMapper.createOrderCommandToRestaurant(createOrderCommand)))
                .thenReturn(Optional.of(restaurantResponse));

        var orderDomainException = assertThrows(OrderDomainException.class, () -> orderApplicationService.createOrder(createOrderCommand));
        assertEquals("Restaurant with id: " + RESTAURANT_ID + " is not currently active.", orderDomainException.getMessage());
    }
}
