package com.food.ordering.system.order.service.dataaccess.restuarant.mapper;

import com.food.ordering.system.order.service.dataaccess.restuarant.entity.RestaurantEntity;
import com.food.ordering.system.order.service.dataaccess.restuarant.exception.RestaurantDataAccessException;
import com.food.ordering.system.order.service.domain.entity.Product;
import com.food.ordering.system.order.service.domain.entity.Restaurant;
import com.food.ordering.system.order.service.domain.valueobject.Money;
import com.food.ordering.system.order.service.domain.valueobject.ProductId;
import com.food.ordering.system.order.service.domain.valueobject.RestaurantId;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class RestaurantDataAccessMapper {
    public List<UUID> toRestaurantProducts(Restaurant restaurant) {
        return restaurant.getProducts().stream()
                .map(p -> p.getId().getValue())
                .collect(Collectors.toList());
    }

    public Restaurant toRestaurant(List<RestaurantEntity> restaurantEntities) {
        var entity = restaurantEntities.stream()
                .findFirst()
                .orElseThrow(() -> new RestaurantDataAccessException("Restaurant could not be found."));

        var products = restaurantEntities.stream()
                .map(e -> new Product(
                                new ProductId(e.getProductId()),
                                e.getProductName(),
                                new Money(e.getProductPrice())
                        )
                ).collect(Collectors.toList());

        return Restaurant.builder()
                .withId(new RestaurantId(entity.getRestaurantId()))
                .withProducts(products)
                .withActive(entity.getRestaurantActive())
                .build();
    }
}
