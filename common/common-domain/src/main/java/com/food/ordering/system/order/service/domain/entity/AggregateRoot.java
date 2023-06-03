package com.food.ordering.system.order.service.domain.entity;

// marker class that extends BaseEntity to reuse setId and getId
public abstract class AggregateRoot<ID> extends BaseEntity<ID> { }
