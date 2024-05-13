package com.promocedes.api.utils;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

public class DecimalFormatter {

    private static final DecimalFormat df = new DecimalFormat(".00");

    public static String formatToTwoDecimalPoints(BigDecimal number) {
        DecimalFormatSymbols symbols = df.getDecimalFormatSymbols();
        symbols.setDecimalSeparator('.');
        df.setDecimalFormatSymbols(symbols);

        return df.format(number);
    }
}
