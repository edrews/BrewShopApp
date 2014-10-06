package com.brew.brewshop.util;

public class Util {

    private static int[] COLOR_MAP = new int[] {
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

    public int getColor(int srm) {
        return getColorNoAlpha(srm) + 0xFF000000;
    }

    private int getColorNoAlpha(int srm) {
        if (srm < 1) {
            return COLOR_MAP[0];
        } else if (srm > COLOR_MAP.length) {
            return COLOR_MAP[COLOR_MAP.length - 1];
        } else {
            return COLOR_MAP[srm - 1];
        }
    }

    public static String fromDouble(double d) {
        return String.format("%.1f", d);
    }
}
