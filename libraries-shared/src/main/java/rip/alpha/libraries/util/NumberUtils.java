package rip.alpha.libraries.util;

import java.math.BigInteger;
import java.text.NumberFormat;

public class NumberUtils {

    private static final NumberFormat BALANCE_FORMAT;

    static {
        BALANCE_FORMAT = NumberFormat.getInstance();
        BALANCE_FORMAT.setGroupingUsed(true);
    }

    public static String formatBalance(BigInteger balance) {
        return BALANCE_FORMAT.format(balance);
    }

    public static String formatBalance(int balance) {
        return BALANCE_FORMAT.format(balance);
    }

    public static double round(double value, int places) {
        double scale = Math.pow(10, places);
        return Math.round(value * scale) / scale;
    }

    public static boolean isInteger(String input) {
        try {
            Integer.parseInt(input);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isShort(String input) {
        try {
            Short.parseShort(input);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
