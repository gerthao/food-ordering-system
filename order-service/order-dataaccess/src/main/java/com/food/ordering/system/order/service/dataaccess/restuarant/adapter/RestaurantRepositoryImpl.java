package com.food.ordering.system.order.service.dataaccess.restuarant.adapter;

import com.food.ordering.system.order.service.dataaccess.restuarant.mapper.RestaurantDataAccessMapper;
import com.food.ordering.system.order.service.dataaccess.restuarant.repository.RestaurantJpaRepository;
import com.food.ordering.system.order.service.domain.entity.Restaurant;
import com.food.ordering.system.order.service.domain.ports.output.repository.RestaurantRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class RestaurantRepositoryImpl implements RestaurantRepository {
    private final RestaurantJpaRepository repository;
    private final RestaurantDataAccessMapper mapper;

    public RestaurantRepositoryImpl(RestaurantJpaRepository repository, RestaurantDataAccessMapper mapper) {
        this.repository = repository;
        this.mapper     = mapper;
    }

    @Override
    public Optional<Restaurant> findRestaurantInformation(Restaurant restaurant) {
        var restaurantProducts = mapper.toRestaurantProducts(restaurant);
        var restaurantEntities = repository.findByRestaurantIdAndProductIdIn(
                restaurant.getId().getValue(), restaurantProducts);

        return restaurantEntities.map(mapper::toRestaurant);
    }
}
