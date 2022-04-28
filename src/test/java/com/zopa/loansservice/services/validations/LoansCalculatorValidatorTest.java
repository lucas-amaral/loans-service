package com.zopa.loansservice.services.validations;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.zopa.loansservice.entities.Lender;

class LoansCalculatorValidatorTest {

    @Test
    void isHundredIncrement() {
        assertThat(LoansCalculatorValidator.isHundredIncrement(BigInteger.valueOf(100))).isTrue();
        assertThat(LoansCalculatorValidator.isHundredIncrement(BigInteger.valueOf(500))).isTrue();
        assertThat(LoansCalculatorValidator.isHundredIncrement(BigInteger.valueOf(700))).isTrue();
        assertThat(LoansCalculatorValidator.isHundredIncrement(BigInteger.valueOf(1100))).isTrue();
        assertThat(LoansCalculatorValidator.isHundredIncrement(BigInteger.valueOf(15000))).isTrue();
    }

    @Test
    void isNotHundredIncrement() {
        assertThat(LoansCalculatorValidator.isHundredIncrement(BigInteger.valueOf(110))).isFalse();
        assertThat(LoansCalculatorValidator.isHundredIncrement(BigInteger.valueOf(560))).isFalse();
        assertThat(LoansCalculatorValidator.isHundredIncrement(BigInteger.valueOf(777))).isFalse();
        assertThat(LoansCalculatorValidator.isHundredIncrement(BigInteger.valueOf(1235))).isFalse();
        assertThat(LoansCalculatorValidator.isHundredIncrement(BigInteger.valueOf(15001))).isFalse();
    }

    @Test
    void isBetweenOneThousandAndFiftyThousand() {
        final BigInteger amount = BigInteger.valueOf(2000);
        assertThat(LoansCalculatorValidator.isBetweenOneThousandAndFiftyThousand(amount)).isTrue();
    }

    @Test
    void isBetweenOneThousandAndFiftyThousandWhenAmountIsExactlyOneThousand() {
        final BigInteger amount = BigInteger.valueOf(1000);
        assertThat(LoansCalculatorValidator.isBetweenOneThousandAndFiftyThousand(amount)).isTrue();
    }

    @Test
    void isBetweenOneThousandAndFiftyThousandWhenAmountIsExactlyFiftyThousand() {
        final BigInteger amount = BigInteger.valueOf(15000);
        assertThat(LoansCalculatorValidator.isBetweenOneThousandAndFiftyThousand(amount)).isTrue();
    }

    @Test
    void isBetweenOneThousandAndFiftyThousandWhenAmountIsLowerThanOneThousand() {
        final BigInteger amount = BigInteger.valueOf(900);
        assertThat(LoansCalculatorValidator.isBetweenOneThousandAndFiftyThousand(amount)).isFalse();
    }

    @Test
    void isBetweenOneThousandAndFiftyThousandWhenAmountIsGreaterThanFiftyThousand() {
        final BigInteger amount = BigInteger.valueOf(15100);
        assertThat(LoansCalculatorValidator.isBetweenOneThousandAndFiftyThousand(amount)).isFalse();
    }

    @Test
    void hasAvailableAmountShouldReturnTrueWhenThereAreEnoughAvailableAmount() {
        final BigInteger amount = BigInteger.valueOf(900);

        final List<Lender> lenders = Arrays.asList(
                Lender.of(null, new BigDecimal("0.069"), new BigInteger("480")),
                Lender.of(null, new BigDecimal("0.071"), new BigInteger("520")));

        assertThat(LoansCalculatorValidator.hasEnoughAmount(lenders, amount)).isTrue();
    }

    @Test
    void hasAvailableAmountShouldReturnTrueWhenAvailableAmountIsEqualToRequestedAmount() {
        final BigInteger amount = BigInteger.valueOf(1000);

        final List<Lender> lenders = Arrays.asList(
                Lender.of(null, new BigDecimal("0.069"), new BigInteger("480")),
                Lender.of(null, new BigDecimal("0.071"), new BigInteger("520")));

        assertThat(LoansCalculatorValidator.hasEnoughAmount(lenders, amount)).isTrue();
    }

    @Test
    void hasAvailableAmountShouldReturnFalseWhenAvailableAmountIsNotEnough() {
        final BigInteger amount = BigInteger.valueOf(1400);

        final List<Lender> lenders = Arrays.asList(
                Lender.of(null, new BigDecimal("0.069"), new BigInteger("480")),
                Lender.of(null, new BigDecimal("0.071"), new BigInteger("520")));

        assertThat(LoansCalculatorValidator.hasEnoughAmount(lenders, amount)).isFalse();
    }
}