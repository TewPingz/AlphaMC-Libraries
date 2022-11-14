package rip.alpha.libraries.util;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Triple<L, M, R> {

    private L left;
    private M middle;
    private R right;

}
