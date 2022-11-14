package rip.alpha.libraries.tests;

import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;

public class TestUtils {

    private static final String CHARS = IntStream.range('!', '~')
            .map(i -> (char) i)
            .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append).toString();

    public static char getRandomChar() {
        return CHARS.charAt(ThreadLocalRandom.current().nextInt(CHARS.length()));
    }

    public static String generateRandomString(int length) {
        StringBuilder builder = new StringBuilder();
        while (builder.length() < length) {
            builder.append(getRandomChar());
        }
        return builder.toString();
    }

}
