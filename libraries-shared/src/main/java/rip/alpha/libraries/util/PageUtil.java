package rip.alpha.libraries.util;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class PageUtil {
    public static <T> List<T> createPage(List<T> stringList, int size, int page) {
        if (size < 0) {
            return Collections.emptyList();
        }
        if (page < 0) {
            return Collections.emptyList();
        }
        return stringList.stream().skip((long) size * page).limit(size).collect(Collectors.toList());
    }

    public static int amountOfPages(List<?> stringList, int size) {
        if (size < 0) {
            return -1;
        }
        return stringList.size() / size;
    }
}
