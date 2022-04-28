package com.zopa.loansservice.services;

import com.zopa.loansservice.entities.Lender;
import com.zopa.loansservice.repositories.LenderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class LenderServiceTest {

    @MockBean
    private LenderRepository lenderRepository;

    private LenderService lenderService;

    @BeforeEach
    void setUp() {
        lenderService = new LenderService(lenderRepository);
    }

    @Test
    void getLendersByAmountWithJustOnePage() {
        final BigInteger amount = new BigInteger("1000");

        final Lender jane = Lender.of(null, new BigDecimal("0.069"), new BigInteger("480"));
        final Lender john = Lender.of(null, new BigDecimal("0.071"), new BigInteger("520"));

        setupLenderSearch(amount, Arrays.asList(jane, john), 0);

        List<Lender> lenders = lenderService.getLendersByAmount(amount);

        assertThat(lenders).containsExactly(jane, john);

        verify(lenderRepository).findAll(any(Pageable.class));
    }

    @Test
    void getLendersByAmountWithMoreThanOnePage() {
        final BigInteger amount = new BigInteger("1000");

        final Lender fred = Lender.of(null, new BigDecimal("0.071"), new BigInteger("520"));
        final Lender mary = Lender.of(null, new BigDecimal("0.104"), new BigInteger("170"));
        final Lender john = Lender.of(null, new BigDecimal("0.081"), new BigInteger("320"));
        final Lender dave = Lender.of(null, new BigDecimal("0.074"), new BigInteger("140"));

        final Sort sortByRate = Sort.by(Sort.Direction.ASC, "rate");
        final PageRequest firstPage = PageRequest.of(0, 50, sortByRate);
        final Page<Lender> lenderFirstPage = mock(PageImpl.class);
        final List<Lender> lendersFirstPage = new ArrayList<>();
        lendersFirstPage.add(fred);
        lendersFirstPage.add(mary);

        setupLenderSearch(amount, Arrays.asList(john, dave), 1);

        when(lenderFirstPage.getContent()).thenReturn(lendersFirstPage);
        when(lenderFirstPage.hasNext()).thenReturn(true);
        when(lenderRepository.findTotalAvailable()).thenReturn(BigInteger.valueOf(1150));
        when(lenderRepository.findAll(firstPage)).thenReturn(lenderFirstPage);

        List<Lender> lenders = lenderService.getLendersByAmount(amount);

        assertThat(lenders).containsExactly(fred, mary, john, dave);

        verify(lenderRepository, times(2)).findAll(any(Pageable.class));
    }

    @Test
    void getLendersWithoutExcessAmountWithExactlyAmount() {
        final BigInteger amount = new BigInteger("1000");

        final List<Lender> lenders = Arrays.asList(
                Lender.of(null, new BigDecimal("0.069"), new BigInteger("480")),
                Lender.of(null, new BigDecimal("0.071"), new BigInteger("520")));

        final List<Lender> selectedLenders = lenderService.getFilteredLowestLendersByAmount(lenders, amount);

        assertThat(selectedLenders).isEqualTo(lenders);
    }

    @Test
    void getLendersWithoutExcessAmountWithExcessAmount() {
        final BigInteger amount = new BigInteger("1000");

        final List<Lender> lenders = Arrays.asList(
                Lender.of(null, new BigDecimal("0.069"), new BigInteger("480")),
                Lender.of(null, new BigDecimal("0.071"), new BigInteger("740")));

        final List<Lender> selectedLenders = lenderService.getFilteredLowestLendersByAmount(lenders, amount);

        assertThat(selectedLenders)
                .isNotEmpty()
                .matches(lender -> lender.get(0).getAvailable().equals(new BigInteger("480")))
                .matches(lender -> lender.get(0).getRate().equals(new BigDecimal("0.069")))
                .matches(lender -> lender.get(1).getAvailable().equals(new BigInteger("520")))
                .matches(lender -> lender.get(1).getRate().equals(new BigDecimal("0.071")));
    }

    private void setupLenderSearch(BigInteger amount, List<Lender> lenders, int pageNumber) {

        final Sort sortByRate = Sort.by(Sort.Direction.ASC, "rate");
        final PageRequest page = PageRequest.of(pageNumber, 50, sortByRate);
        final Page<Lender> lenderPage = new PageImpl<>(lenders, page, 1);

        when(lenderRepository.findTotalAvailable()).thenReturn(amount);
        when(lenderRepository.findAll(page)).thenReturn(lenderPage);
    }
}