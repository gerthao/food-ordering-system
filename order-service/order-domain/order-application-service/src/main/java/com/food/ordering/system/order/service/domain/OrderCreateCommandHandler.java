package com.food.ordering.system.order.service.domain;

import com.food.ordering.system.order.service.domain.dto.create.CreateOrderCommand;
import com.food.ordering.system.order.service.domain.dto.create.CreateOrderResponse;
import com.food.ordering.system.order.service.domain.entity.Order;
import com.food.ordering.system.order.service.domain.entity.Restaurant;
import com.food.ordering.system.order.service.domain.exception.OrderDomainException;
import com.food.ordering.system.order.service.domain.mapper.OrderDataMapper;
import com.food.ordering.system.order.service.domain.ports.output.repository.CustomerRepository;
import com.food.ordering.system.order.service.domain.ports.output.repository.OrderRepository;
import com.food.ordering.system.order.service.domain.ports.output.repository.RestaurantRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.message.Message;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.text.MessageFormat;
import java.util.UUID;

@Slf4j
@Component
public class OrderCreateCommandHandler {
    private final OrderDomainService   orderDomainService;
    private final OrderRepository      orderRepository;
    private final CustomerRepository   customerRepository;
    private final RestaurantRepository restaurantRepository;
    private final OrderDataMapper      orderDataMapper;

    public OrderCreateCommandHandler(OrderDomainService orderDomainService, OrderRepository orderRepository, CustomerRepository customerRepository, RestaurantRepository restaurantRepository, OrderDataMapper orderDataMapper) {
        this.orderDomainService   = orderDomainService;
        this.orderRepository      = orderRepository;
        this.customerRepository   = customerRepository;
        this.restaurantRepository = restaurantRepository;
        this.orderDataMapper      = orderDataMapper;
    }

    @Transactional
    public CreateOrderResponse createOrder(CreateOrderCommand createOrderCommand) {
        checkCustomer(createOrderCommand.customerId());
        var restaurant = checkRestaurant(createOrderCommand);
        var order = orderDataMapper.createOrderCommandToOrder(createOrderCommand);
        var orderCreatedEvent = orderDomainService.validateAndInitializeOrder(order, restaurant);
        var saveOrderResult = saveOrder(order);
        log.info(MessageFormat.format("Order is created with id: {0}", saveOrderResult.getId().getValue()));
    }

    private Restaurant checkRestaurant(CreateOrderCommand createOrderCommand) {
        var restaurant      = orderDataMapper.createOrderCommandToRestaurant(createOrderCommand);
        var maybeRestaurant = restaurantRepository.findRestaurantInformation(restaurant);
        if (maybeRestaurant.isEmpty()) {
            log.warn(MessageFormat.format("Could not find customer with restaurant id: {0}", createOrderCommand.restaurantId()));
            throw new OrderDomainException(MessageFormat.format("Could not find restaurant with restaurant id: {0}", createOrderCommand.restaurantId());
        }
        return maybeRestaurant.get();
    }

    private void checkCustomer(UUID customerId) throws OrderDomainException {
        var maybeCustomer = customerRepository.findCustomer(customerId);
        if (maybeCustomer.isEmpty()) {
            log.warn(MessageFormat.format("Could not find customer with customer id: {0}", customerId));
            throw new OrderDomainException(MessageFormat.format("Could not find customer with customer id: {0}", customerId));
        }
    }

    private Order saveOrder(Order order) {
        Order saveOrderResult = orderRepository.save(order);
        if (saveOrderResult == null) {
            log.error("Could not save order");
            throw new OrderDomainException("Could not save order");
        }
        log.info(MessageFormat.format("Order is saved with id: {0}", saveOrderResult.getId().getValue()));
        return saveOrderResult;
    }
}
