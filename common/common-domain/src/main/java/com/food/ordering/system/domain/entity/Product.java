package com.food.ordering.system.domain.entity;

import com.food.ordering.system.domain.valueobject.Money;
import com.food.ordering.system.domain.valueobject.ProductId;

public class Product extends BaseEntity<ProductId> {
    private String name;
    private Money  money;

    public Product(ProductId productId, String name, Money money) {
        super.setId(productId);
        this.name  = name;
        this.money = money;
    }
}
