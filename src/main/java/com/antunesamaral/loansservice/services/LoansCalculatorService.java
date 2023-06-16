package com.antunesamaral.loansservice.services;

import com.antunesamaral.loansservice.entities.Lender;
import com.antunesamaral.loansservice.entities.Loans;
import com.antunesamaral.loansservice.exceptions.LoansException;
import com.antunesamaral.loansservice.services.validations.LoansCalculatorValidator;
import org.assertj.core.util.VisibleForTesting;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class LoansCalculatorService {

    private static final BigDecimal LOAN_TIME_IN_MONTHS = BigDecimal.valueOf(36);
    private static final BigDecimal DAYS_OF_MONTH = BigDecimal.valueOf(30);
    private static final BigDecimal DAYS_OF_YEAR = BigDecimal.valueOf(360);
    private static final int ONE_HUNDRED_PER_CENT = 1;

    private final LenderService lenderService;

    public LoansCalculatorService(LenderService lenderService) {
        this.lenderService = lenderService;
    }

    public void lowestInterestLoans(@NonNull BigInteger amount) {
        final Optional<Loans> loansOpt = getLowestLoansByAmount(amount);

        if (!loansOpt.isPresent()) {
            System.out.println("It is not possible to provide a quote.");
        }

        loansOpt.ifPresent(loans -> {
            System.out.println("Requested amount: " + loans.getRequestedAmount());
            System.out.println("Annual Interest Rate: " + loans.getAnnualInterestRate());
            System.out.println("Monthly repayment: " + loans.getMonthlyRepayment());
            System.out.println("Total repayment: " + loans.getTotalRepayment());
        });
    }

    @Transactional(readOnly = true)
    public Optional<Loans> getLowestLoansByAmount(BigInteger amount) {
        if (!LoansCalculatorValidator.isValidAmount(amount)) {
            throw new LoansException("Invalid requested amount. Please, inform any £100 increment between £1000 and £15000 inclusive.");
        }

        final Map<Lender, BigDecimal> lendersWithLowestRates = getLowestLendersRateByAmount(amount);

        return CollectionUtils.isEmpty(lendersWithLowestRates) ? Optional.empty()
                : generateLoans(amount, lendersWithLowestRates);
    }

    private Map<Lender, BigDecimal> getLowestLendersRateByAmount(BigInteger amount) {
        final List<Lender> lenders = lenderService.getLendersByAmount(amount);

        return lenderService.getFilteredLowestLendersByAmount(lenders, amount).stream()
                .collect(Collectors.toMap(Function.identity(), this::calculateMonthlyRepayment));
    }

    private Optional<Loans> generateLoans(BigInteger amount, Map<Lender, BigDecimal> lendersWithLowestRates) {
        final BigDecimal monthlyRepayment = lendersWithLowestRates.values().stream()
                .reduce(BigDecimal::add)
                .orElseThrow(() -> new LoansException("Error to calculated monthly repayment"));
        final BigDecimal annualInterestRate = lendersWithLowestRates.keySet().stream()
                .map(Lender::getRate)
                .reduce(BigDecimal::add)
                .orElse(BigDecimal.ZERO)
                .divide(BigDecimal.valueOf(lendersWithLowestRates.size()), MathContext.DECIMAL64);
        final BigDecimal totalRepayment = monthlyRepayment.multiply(LOAN_TIME_IN_MONTHS);

        return Optional.of(Loans.of(amount, annualInterestRate, monthlyRepayment, totalRepayment));
    }

    /**
     * Creates a {@link BigDecimal} with a periodic payment amount
     * Formula: (amount * monthly rate) / 1 - (1 + monthly rate)^ - loan time
     *
     * @param lender the lender contains the annual interest rate and also the available money
     * @return {@code BigDecimal}
     */
    @VisibleForTesting
    BigDecimal calculateMonthlyRepayment(Lender lender) {
        final BigDecimal monthlyRate = getMonthlyRateFromAnnualRate(lender.getRate());
        final BigDecimal amount = new BigDecimal(lender.getAvailable());

        final BigDecimal divisor = BigDecimal.ONE.subtract(BigDecimal.ONE
                .add(monthlyRate)
                .pow(LOAN_TIME_IN_MONTHS.intValue() * -1, MathContext.DECIMAL64));

        return amount.multiply(monthlyRate)
                .divide(divisor, MathContext.DECIMAL64);
    }

    /**
     * Creates a {@link BigDecimal} with the monthly interest rate by annual rate
     * Formula: (1 + annual rate)^(30 / 360) - 1
     *
     * @param annualRate the annual rate configured for each lender
     * @return {@code BigDecimal}
     */
    private BigDecimal getMonthlyRateFromAnnualRate(@NonNull BigDecimal annualRate) {
        final double monthlyRate = Math.pow(annualRate.add(BigDecimal.ONE).doubleValue(),
                DAYS_OF_MONTH.divide(DAYS_OF_YEAR, MathContext.DECIMAL64).doubleValue()) - ONE_HUNDRED_PER_CENT;
        return BigDecimal.valueOf(monthlyRate);
    }

}
