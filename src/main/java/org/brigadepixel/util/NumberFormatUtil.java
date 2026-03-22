package org.brigadepixel.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class NumberFormatUtil {

    public static double round(double value, int decimals) {
        if (decimals < 0) {
            throw new IllegalArgumentException("Decimals must be >= 0");
        }

        return BigDecimal
                .valueOf(value)
                .setScale(decimals, RoundingMode.HALF_UP)
                .doubleValue();
    }

    public static double round2(double value) {
        return round(value, 2);
    }
}
