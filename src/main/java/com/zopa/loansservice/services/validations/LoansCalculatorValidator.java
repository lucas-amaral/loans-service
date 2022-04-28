package com.zopa.loansservice.services.validations;

import java.math.BigInteger;
import java.util.List;

import com.zopa.loansservice.entities.Lender;

public class LoansCalculatorValidator {

    private static final BigInteger ONE_HUNDRED = BigInteger.valueOf(100);
    private static final BigInteger ONE_THOUSAND = BigInteger.valueOf(1000);
    private static final BigInteger FIFTY_THOUSAND = BigInteger.valueOf(15000);

    public static boolean isValidAmount(BigInteger amount) {
        return isHundredIncrement(amount) && isBetweenOneThousandAndFiftyThousand(amount);
    }

    public static boolean isHundredIncrement(BigInteger amount) {
        return amount.remainder(ONE_HUNDRED).equals(BigInteger.ZERO);
    }

    public static boolean isBetweenOneThousandAndFiftyThousand(BigInteger amount) {
        return amount.compareTo(ONE_THOUSAND) >= 0 && amount.compareTo(FIFTY_THOUSAND) <= 0;
    }

    public static boolean hasEnoughAmount(List<Lender> lenders, BigInteger amount) {
        final BigInteger lendersAmount = lenders.stream()
                .map(Lender::getAvailable)
                .reduce(BigInteger::add)
                .orElse(BigInteger.ZERO);
        return lendersAmount.compareTo(amount) >= 0;
    }
}
