package com.food.ordering.system.order.service.dataaccess.order.adapter;

import com.food.ordering.system.order.service.dataaccess.order.mapper.OrderDataAccessMapper;
import com.food.ordering.system.order.service.dataaccess.order.repository.OrderJpaRepository;
import com.food.ordering.system.order.service.domain.entity.Order;
import com.food.ordering.system.order.service.domain.ports.output.repository.OrderRepository;
import com.food.ordering.system.order.service.domain.valueobject.TrackingId;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.stream.Stream;

@Component
public class OrderRepositoryImpl implements OrderRepository {
    private final OrderJpaRepository repository;
    private final OrderDataAccessMapper mapper;

    public OrderRepositoryImpl(OrderJpaRepository repository, OrderDataAccessMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Order save(Order order) {
        return Stream.of(order)
                .map(mapper::orderToOrderEntity)
                .map(repository::save)
                .map(mapper::orderEntityToOrder)
                .findFirst()
                .get();
    }

    @Override
    public Optional<Order> findByTrackingId(TrackingId trackingId) {
        return repository.findByTrackingId(trackingId.getValue())
                .map(mapper::orderEntityToOrder);
    }
}
