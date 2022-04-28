package com.zopa.loansservice.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import com.zopa.loansservice.repositories.LenderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.zopa.loansservice.entities.Lender;
import com.zopa.loansservice.entities.Loans;
import com.zopa.loansservice.exceptions.LoansException;

@ExtendWith(SpringExtension.class)
class LoansCalculatorServiceTest {

    @MockBean
    private LenderRepository lenderRepository;

    private LoansCalculatorService loansCalculatorService;

    @BeforeEach
    void setUp() {
        LenderService lenderService = new LenderService(lenderRepository);
        loansCalculatorService = new LoansCalculatorService(lenderService);
    }


    @Test
    void lowestInterestLoansWithInvalidAmount() {
        final BigInteger amount = new BigInteger("1150");

        assertThrows(LoansException.class, () -> loansCalculatorService.lowestInterestLoans(amount));

        verifyNoInteractions(lenderRepository);
    }

    @Test
    void lowestInterestLoans() {
        final BigInteger amount = new BigInteger("1100");

        final Lender jane = Lender.of(null, new BigDecimal("0.069"), new BigInteger("480"));
        final Lender john = Lender.of(null, new BigDecimal("0.071"), new BigInteger("620"));
        final List<Lender> lenders = Arrays.asList(jane, john);

        setupLenderSearches(amount, lenders);

        loansCalculatorService.lowestInterestLoans(amount);

        verify(lenderRepository).findTotalAvailable();
        verify(lenderRepository).findAll(any(Pageable.class));
        verifyNoMoreInteractions(lenderRepository);
    }

    @Test
    void getLowestLoansByAmountWhenLendersAmountIsEqualToRequestedAmount() {
        final BigInteger amount = new BigInteger("1000");

        final Lender jane = Lender.of(null, new BigDecimal("0.069"), new BigInteger("480"));
        final Lender john = Lender.of(null, new BigDecimal("0.071"), new BigInteger("520"));
        final List<Lender> lenders = Arrays.asList(jane, john);

        setupLenderSearches(amount, lenders);

        final Optional<Loans> loans = loansCalculatorService.getLowestLoansByAmount(amount);

        assertThat(loans)
                .isPresent()
                .get()
                .matches(loan -> loan.getRequestedAmount().equals("£1000"))
                .matches(loan -> loan.getAnnualInterestRate().equals("7.0%"))
                .matches(loan -> loan.getMonthlyRepayment().equals("£30.78"))
                .matches(loan -> loan.getTotalRepayment().equals("£1108.10"));
    }

    @Test
    void getLowestLoansByAmountWhenLendersAmountIsGreaterThanRequestedAmount() {
        final BigInteger amount = new BigInteger("1700");

        final Lender bob = Lender.of(null, new BigDecimal("0.075"), new BigInteger("640"));
        final Lender jane = Lender.of(null, new BigDecimal("0.069"), new BigInteger("480"));
        final Lender fred = Lender.of(null, new BigDecimal("0.071"), new BigInteger("520"));
        final Lender mary = Lender.of(null, new BigDecimal("0.104"), new BigInteger("170"));
        final Lender john = Lender.of(null, new BigDecimal("0.081"), new BigInteger("320"));
        final Lender dave = Lender.of(null, new BigDecimal("0.074"), new BigInteger("140"));
        final Lender angela = Lender.of(null, new BigDecimal("0.071"), new BigInteger("60"));
        final List<Lender> lenders = Arrays.asList(jane, angela, fred, dave, bob, john, mary);

        setupLenderSearches(amount, lenders);

        final Optional<Loans> loans = loansCalculatorService.getLowestLoansByAmount(amount);

        assertThat(loans)
                .isPresent()
                .get()
                .matches(loan -> loan.getRequestedAmount().equals("£1700"))
                .matches(loan -> loan.getAnnualInterestRate().equals("7.2%"))
                .matches(loan -> loan.getMonthlyRepayment().equals("£52.46"))
                .matches(loan -> loan.getTotalRepayment().equals("£1888.55"));
    }

    @Test
    void getLowestLoansByAmountWithoutEnoughAvailableAmount() {
        final BigInteger amount = new BigInteger("1100");

        when(lenderRepository.findTotalAvailable()).thenReturn(BigInteger.valueOf(1000));

        final Optional<Loans> loans = loansCalculatorService.getLowestLoansByAmount(amount);

        assertThat(loans).isNotPresent();
    }

    @Test
    void calculateMonthlyRepayment() {
        final Lender lender = Lender.of(null, new BigDecimal("0.069"), new BigInteger("480"));

        final BigDecimal monthlyRepayment = loansCalculatorService.calculateMonthlyRepayment(lender);

        assertThat(monthlyRepayment).isEqualTo(new BigDecimal("14.75327502897265"));
    }

    private void setupLenderSearches(BigInteger amount, List<Lender> lenders) {
        final Sort sortByRate = Sort.by(Sort.Direction.ASC, "rate");
        final PageRequest page = PageRequest.of(0, 50, sortByRate);

        when(lenderRepository.findTotalAvailable()).thenReturn(amount);
        when(lenderRepository.findAll(page)).thenReturn(new PageImpl<>(lenders));
    }
}