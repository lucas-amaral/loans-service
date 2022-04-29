package com.zopa.loansservice.services;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import com.zopa.loansservice.entities.Lender;
import com.zopa.loansservice.repositories.LenderRepository;
import com.zopa.loansservice.services.validations.LoansCalculatorValidator;

@Service
public class LenderService {

    private static final Sort SORT_BY_RATE = Sort.by(Sort.Direction.ASC, "rate"); //just a new comment
    private static final int PAGE_SIZE = 50;
    private static final int PAGE_NUMBER = 0;

    private final LenderRepository lenderRepository;

    public LenderService(LenderRepository lenderRepository) {
        this.lenderRepository = lenderRepository;
    }

    public List<Lender> getLendersByAmount(@NonNull BigInteger amount) {
        if (!hasAvailableAmount(amount)) {
            return Collections.emptyList();
        }
        return getLendersByAmount(amount, PAGE_NUMBER);
    }

    /**
     * Returns a {@link List} with the lenders ordered by interest rate.
     *
     * To avoid loading all data from lenders, the search is done by pages.
     * If the value found on the page does not have enough amount for the loan, a new page will be searched and added to the current one.
     *
     * @param amount the requested amount
     * @param pageNumber the page number requested
     * @return {@code List}
     */
    private List<Lender> getLendersByAmount(BigInteger amount, int pageNumber) {
        final PageRequest page = PageRequest.of(pageNumber, PAGE_SIZE, SORT_BY_RATE);
        final Page<Lender> lendersPage = lenderRepository.findAll(page);

        if (needMoreLendersAmount(amount, lendersPage)) {
            getLendersByAmount(amount, ++pageNumber).forEach(lender -> lendersPage.getContent().add(lender));
        }

        return lendersPage.getContent();
    }

    private boolean needMoreLendersAmount(BigInteger amount, Page<Lender> lendersPage) {
        return !LoansCalculatorValidator.hasEnoughAmount(lendersPage.getContent(), amount) && lendersPage.hasNext();
    }

    private boolean hasAvailableAmount(BigInteger amount) {
        final BigInteger totalAvailable = lenderRepository.findTotalAvailable();
        return amount.compareTo(totalAvailable) <= 0;
    }

    /**
     * Creates a {@link List} just with the lenders that has the lowest interest rate.
     * In addition to removing the lenders that will not be needed, if necessary, the lender
     * with the highest rate among those chosen will be updated
     * to remove the excess amount that exceeds the requested amount
     *
     * @param lenders the list with the lenders
     * @param amount the requested amount
     * @return {@code List}
     */
    public List<Lender> getFilteredLowestLendersByAmount(@NonNull List<Lender> lenders, @NonNull BigInteger amount) {
        final List<Lender> lendersWithLowestRate = new ArrayList<>();
        final AtomicReference<BigInteger> lendersAmount = new AtomicReference<>(BigInteger.ZERO);

        for (Lender lender : lenders) {
            if (lender.getAvailable().add(lendersAmount.get()).compareTo(amount) <= 0) {
                lendersAmount.set(lendersAmount.get().add(lender.getAvailable()));
                lendersWithLowestRate.add(lender);
            } else {
                final BigInteger lenderAmount = amount.subtract(lendersAmount.get());
                lendersWithLowestRate.add(Lender.of(lender.getName(), lender.getRate(), lenderAmount));
                break;
            }
        }

        return lendersWithLowestRate;
    }
}
