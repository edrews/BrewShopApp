package com.brew.brewshop.util;

import java.math.BigDecimal;

public class Util {

    private static double[][] HOP_UTILIZATION = new double[][] {
            {0.000, 0.000, 0.000, 0.000, 0.000, 0.000, 0.000, 0.000, 0.000, 0.000},
            {0.055, 0.050, 0.046, 0.042, 0.038, 0.035, 0.032, 0.029, 0.027, 0.025},
            {0.100, 0.091, 0.084, 0.076, 0.070, 0.064, 0.058, 0.053, 0.049, 0.045},
            {0.137, 0.125, 0.114, 0.105, 0.096, 0.087, 0.080, 0.073, 0.067, 0.061},
            {0.167, 0.153, 0.140, 0.128, 0.117, 0.107, 0.098, 0.089, 0.081, 0.074},
            {0.192, 0.175, 0.160, 0.147, 0.134, 0.122, 0.112, 0.102, 0.094, 0.085},
            {0.212, 0.194, 0.177, 0.162, 0.148, 0.135, 0.124, 0.113, 0.103, 0.094},
            {0.229, 0.209, 0.191, 0.175, 0.160, 0.146, 0.133, 0.122, 0.111, 0.102},
            {0.242, 0.221, 0.202, 0.185, 0.169, 0.155, 0.141, 0.129, 0.118, 0.108},
            {0.253, 0.232, 0.212, 0.194, 0.177, 0.162, 0.148, 0.135, 0.123, 0.113},
            {0.263, 0.240, 0.219, 0.200, 0.183, 0.168, 0.153, 0.140, 0.128, 0.117},
            {0.270, 0.247, 0.226, 0.206, 0.188, 0.172, 0.157, 0.144, 0.132, 0.120},
            {0.276, 0.252, 0.231, 0.211, 0.193, 0.176, 0.161, 0.147, 0.135, 0.123},
            {0.285, 0.261, 0.238, 0.218, 0.199, 0.182, 0.166, 0.152, 0.139, 0.127},
            {0.291, 0.266, 0.243, 0.222, 0.203, 0.186, 0.170, 0.155, 0.142, 0.130},
            {0.295, 0.270, 0.247, 0.226, 0.206, 0.188, 0.172, 0.157, 0.144, 0.132},
            {0.298, 0.272, 0.249, 0.228, 0.208, 0.190, 0.174, 0.159, 0.145, 0.133},
            {0.300, 0.274, 0.251, 0.229, 0.209, 0.191, 0.175, 0.160, 0.146, 0.134},
            {0.301, 0.275, 0.252, 0.230, 0.210, 0.192, 0.176, 0.161, 0.147, 0.134}
    };

    private static double[] HOP_TIMES = new double[] {
            0, 5, 10, 15, 20, 25, 30, 35, 40, 45, 50, 55, 60, 70, 80, 90, 100, 110, 120
    };

    private static double[] HOP_GRAVITIES= new double[] {
            30, 40, 50, 60, 70, 80, 90, 100, 110, 120
    };

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

    public static double getHopUtilization(double minutes, double gravity) {
        double adjusted = (gravity - 1) * 1000;
        int minutesIdx = findClosestIdx(minutes, HOP_TIMES);
        int gravityIdx = findClosestIdx(adjusted, HOP_GRAVITIES);
        return HOP_UTILIZATION[minutesIdx][gravityIdx];
    }

    private static int findClosestIdx(double value, double[] values) {
        int idx = 0;
        double smallestDifference = Math.abs(values[0] - value);
        for(int i = 0; i < values.length; i++){
            double difference = Math.abs(values[i] - value);
            if (difference < smallestDifference){
                idx = i;
                smallestDifference = difference;
            }
        }
        return idx;
    }

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

    public static int toInt(CharSequence value) {
        if (value.length() == 0) {
            return 0;
        }
        return Integer.parseInt(value.toString());
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
        String string;
        if (stripZeros) {
            if (d < .001) { //Fix a bug in Java 7
                string = "0";
            } else {
                string = bd.stripTrailingZeros().toPlainString();
            }
        } else {
            string = bd.toPlainString();
        }
        return string;
    }

    public static String separateSentences(String paragraph) {
        return paragraph.replace(". ", ".\n\n");
    }
}
