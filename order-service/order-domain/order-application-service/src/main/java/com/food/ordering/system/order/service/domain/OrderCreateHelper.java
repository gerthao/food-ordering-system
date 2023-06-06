package com.food.ordering.system.order.service.domain;

import com.food.ordering.system.order.service.domain.dto.create.CreateOrderCommand;
import com.food.ordering.system.order.service.domain.entity.Order;
import com.food.ordering.system.order.service.domain.entity.Restaurant;
import com.food.ordering.system.order.service.domain.event.OrderCreatedEvent;
import com.food.ordering.system.order.service.domain.exception.OrderDomainException;
import com.food.ordering.system.order.service.domain.mapper.OrderDataMapper;
import com.food.ordering.system.order.service.domain.ports.output.repository.CustomerRepository;
import com.food.ordering.system.order.service.domain.ports.output.repository.OrderRepository;
import com.food.ordering.system.order.service.domain.ports.output.repository.RestaurantRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.text.MessageFormat;
import java.util.UUID;

@Slf4j
@Component
public class OrderCreateHelper {
    private final OrderDomainService   orderDomainService;
    private final OrderRepository      orderRepository;
    private final CustomerRepository   customerRepository;
    private final RestaurantRepository restaurantRepository;
    private final OrderDataMapper      orderDataMapper;

    public OrderCreateHelper(OrderDomainService orderDomainService, OrderRepository orderRepository, CustomerRepository customerRepository, RestaurantRepository restaurantRepository, OrderDataMapper orderDataMapper) {
        this.orderDomainService   = orderDomainService;
        this.orderRepository      = orderRepository;
        this.customerRepository   = customerRepository;
        this.restaurantRepository = restaurantRepository;
        this.orderDataMapper      = orderDataMapper;
    }

    @Transactional
    public OrderCreatedEvent persistOrder(CreateOrderCommand createOrderCommand) {
        checkCustomer(createOrderCommand.customerId());
        var restaurant        = checkRestaurant(createOrderCommand);
        var order             = orderDataMapper.createOrderCommandToOrder(createOrderCommand);
        var orderCreatedEvent = orderDomainService.validateAndInitializeOrder(order, restaurant);
        saveOrder(order);
        log.info(MessageFormat.format("Order is created with id: {1}", order.getId().getValue()));
        return orderCreatedEvent;
    }

    private Restaurant checkRestaurant(CreateOrderCommand createOrderCommand) {
        var restaurant      = orderDataMapper.createOrderCommandToRestaurant(createOrderCommand);
        var maybeRestaurant = restaurantRepository.findRestaurantInformation(restaurant);

        return maybeRestaurant.orElseThrow(() -> {
            log.warn(MessageFormat.format("Could not find customer with restaurant id: {0}", createOrderCommand.restaurantId()));
            return new OrderDomainException(MessageFormat.format("Could not find restaurant with restaurant id: {0}", createOrderCommand.restaurantId()));
        });
    }

    private void checkCustomer(UUID customerId) throws OrderDomainException {
        customerRepository.findCustomer(customerId).orElseThrow(() -> {
            log.warn(MessageFormat.format("Could not find customer with customer id: {0}", customerId));
            return new OrderDomainException(MessageFormat.format("Could not find customer with customer id: {0}", customerId));
        });
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
