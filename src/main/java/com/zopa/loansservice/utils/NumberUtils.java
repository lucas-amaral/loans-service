package com.zopa.loansservice.utils;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public class NumberUtils {
    private static final DecimalFormat CURRENCY_FORMATTER = new DecimalFormat("£#####.00");
    private static final DecimalFormat CURRENCY_FORMATTER_WITHOUT_SCALE = new DecimalFormat("£#####");
    private static final NumberFormat PERCENTAGE_FORMATTER = NumberFormat.getPercentInstance();


    public static String formatToMonetary(Number amount) {
        return CURRENCY_FORMATTER.format(amount);
    }

    public static String formatToMonetaryWithoutScale(Number amount) {
        return CURRENCY_FORMATTER_WITHOUT_SCALE.format(amount);
    }

    public static String formatToPercentage(Number number) {
        PERCENTAGE_FORMATTER.setMinimumFractionDigits(1);
        return PERCENTAGE_FORMATTER.format(number);
    }
}
