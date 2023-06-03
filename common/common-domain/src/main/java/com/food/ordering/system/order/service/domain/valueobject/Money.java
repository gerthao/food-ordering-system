package com.food.ordering.system.order.service.domain.valueobject;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;
import java.util.function.Function;

public class Money {
    public static final Money      ZERO = new Money(BigDecimal.ZERO);
    private final       BigDecimal amount;

    public Money(BigDecimal amount) {
        this.amount = amount;
    }

    public boolean isGreaterThanZero() {
        return this.amount != null && this.amount.compareTo(BigDecimal.ZERO) > 0;
    }

    public boolean isGreaterThan(Money money) {
        return this.amount != null && this.amount.compareTo(money.getAmount()) > 0;
    }

    public Money add(Money money) {
        return operation(money, this.amount::add);
    }

    public Money subtract(Money money) {
        return operation(money, this.amount::subtract);
    }

    public Money multiply(int multiplier) {
        return operation(new Money(new BigDecimal(multiplier)), this.amount::multiply);
    }

    public BigDecimal getAmount() {
        return amount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Money money = (Money) o;
        return Objects.equals(amount, money.amount);
    }

    @Override
    public int hashCode() {
        return Objects.hash(amount);
    }

    private BigDecimal setScale(BigDecimal input) {
        return input.setScale(2, RoundingMode.HALF_EVEN);
    }

    private Money operation(Money money, Function<BigDecimal, BigDecimal> op) {
        var resultingAmount = setScale(op.apply(money.getAmount()));
        return new Money(resultingAmount);
    }
}
