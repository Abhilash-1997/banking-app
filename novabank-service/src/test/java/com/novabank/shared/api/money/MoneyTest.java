package com.novabank.shared.api.money;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Currency;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MoneyTest {

    private static final Currency INR = Currency.getInstance("INR");
    private static final Currency EUR = Currency.getInstance("EUR");

    @Test
    void of_normalizesScaleWithBankersRounding() {
        Money money = Money.of(new BigDecimal("10.555"), INR);
        assertThat(money.amount()).isEqualByComparingTo("10.56");
        assertThat(money.currency()).isEqualTo(INR);
    }

    @Test
    void plus_sameCurrency_adds() {
        Money sum = Money.of("10.00", "INR").plus(Money.of("2.50", "INR"));
        assertThat(sum).isEqualTo(Money.of("12.50", "INR"));
    }

    @Test
    void minus_sameCurrency_subtracts() {
        Money result = Money.of("10.00", "INR").minus(Money.of("2.50", "INR"));
        assertThat(result.amount()).isEqualByComparingTo("7.50");
    }

    @Test
    void plus_differentCurrency_fails() {
        assertThatThrownBy(() -> Money.of("1.00", "INR").plus(Money.of("1.00", "EUR")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Currency mismatch");
    }

    @Test
    void isZero_and_negated() {
        assertThat(Money.zero(INR).isZero()).isTrue();
        assertThat(Money.of("1.00", "INR").negated().isNegative()).isTrue();
        assertThat(Money.of("1.00", "INR").isPositive()).isTrue();
    }

    @Test
    void equals_ignoresScaleWhenValuesMatch() {
        assertThat(Money.of(new BigDecimal("10.0"), INR)).isEqualTo(Money.of(new BigDecimal("10.00"), INR));
        assertThat(Money.of("1.00", "INR")).isNotEqualTo(Money.of("1.00", "EUR"));
        assertThat(EUR).isNotNull();
    }

    @Test
    void of_rejectsNullAmount() {
        assertThatThrownBy(() -> Money.of((BigDecimal) null, INR))
                .isInstanceOf(NullPointerException.class);
    }
}
