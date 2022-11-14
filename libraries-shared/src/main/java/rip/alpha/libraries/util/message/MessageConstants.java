package rip.alpha.libraries.util.message;

import java.util.Set;
import java.util.regex.Pattern;

public final class MessageConstants {

    public static final char COLOR_SYMBOL = '§';
    public static final Pattern ALPHANUMERIC_PATTERN = Pattern.compile("[^a-zA-Z0-9]");
    private static final Set<String> VOWELS = Set.of("a", "e", "u", "i", "o");

    public static String getAOrAn(String input) {
        return ((VOWELS.contains(input.substring(0, 1).toLowerCase())) ? "an" : "a");
    }

    public static String getSorApostrophe(String in) {
        return in.endsWith("s") ? "'" : "'s";
    }

    public static String completeWithSOrApostrophe(String in) {
        return in + getSorApostrophe(in);
    }

}
