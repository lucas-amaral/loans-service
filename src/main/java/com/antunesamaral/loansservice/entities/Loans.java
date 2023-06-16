package com.antunesamaral.loansservice.entities;

import com.antunesamaral.loansservice.utils.NumberUtils;

import java.math.BigDecimal;
import java.math.BigInteger;

public class Loans {
    private final String requestedAmount;
    private final String annualInterestRate;
    private final String monthlyRepayment;
    private final String totalRepayment;

    public Loans(String requestedAmount, String annualInterestRate, String monthlyRepayment, String totalRepayment) {
        this.requestedAmount = requestedAmount;
        this.annualInterestRate = annualInterestRate;
        this.monthlyRepayment = monthlyRepayment;
        this.totalRepayment = totalRepayment;
    }

    public static Loans of(BigInteger amount, BigDecimal rate, BigDecimal monthlyRepayment, BigDecimal totalRepayment) {
        final String requestedAmount = NumberUtils.formatToMonetaryWithoutScale(amount);
        final String annualInterestRate = NumberUtils.formatToPercentage(rate);
        final String monthlyRepaymentFormatted = NumberUtils.formatToMonetary(monthlyRepayment);
        final String totalRepaymentFormatted = NumberUtils.formatToMonetary(totalRepayment);

        return new Loans(requestedAmount, annualInterestRate, monthlyRepaymentFormatted, totalRepaymentFormatted);
    }

    public String getRequestedAmount() {
        return requestedAmount;
    }

    public String getAnnualInterestRate() {
        return annualInterestRate;
    }

    public String getMonthlyRepayment() {
        return monthlyRepayment;
    }

    public String getTotalRepayment() {
        return totalRepayment;
    }
}
