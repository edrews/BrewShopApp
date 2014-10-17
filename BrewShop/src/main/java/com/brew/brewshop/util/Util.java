package com.brew.brewshop.util;

import java.math.BigDecimal;

public class Util {

    private static int[] SRM_COLORS = new int[] {
        0xF3F993,
        0xF5F75C,
        0xF6F513,
        0xEAE615,
        0xE0D01B,
        0xD5BC26,
        0xCDAA37,
        0xC1963C,
        0xBE8C3A,
        0xBE823A,
        0xC17A37,
        0xBF7138,
        0xBC6733,
        0xB26033,
        0xA85839,
        0x985336,
        0x8D4C32,
        0x7C452D,
        0x6B3A1E,
        0x5D341A,
        0x4E2A0C,
        0x4A2727,
        0x361F1B,
        0x261716,
        0x231716,
        0x19100F,
        0x16100F,
        0x120D0C,
        0x100B0A,
        0x050B0A
    };

    public static int getColor(double srm) {
        return getColorNoAlpha(srm) + 0xFF000000;
    }

    private static int getColorNoAlpha(double srm) {
        int rounded = (int) Math.round(srm);
        if (rounded < 1) {
            return SRM_COLORS[0];
        } else if (rounded > SRM_COLORS.length) {
            return SRM_COLORS[SRM_COLORS.length - 1];
        } else {
            return SRM_COLORS[rounded - 1];
        }
    }

    public static double toDouble(CharSequence value) {
        if (value.length() == 0) {
            return 0;
        }
        return Double.parseDouble(value.toString());
    }

    public static String fromDouble(double d, int precision) {
        return fromDouble(d, precision, true);
    }

    public static String fromDouble(double d, int precision, boolean stripZeros) {
        BigDecimal bd = new BigDecimal(d).setScale(precision, BigDecimal.ROUND_HALF_EVEN);
        if (stripZeros) {
            bd = bd.stripTrailingZeros();
        }
        return bd.toPlainString();
    }

    public static String separateSentences(String paragraph) {
        return paragraph.replace(". ", ".\n\n");
    }
}
