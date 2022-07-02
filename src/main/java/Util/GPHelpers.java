package Util;

import java.util.Random;

public final class GPHelpers {
    public static int randomIndex (int size) {
        if (size == 1) return 0;
        return new Random().nextInt(size);
    }
}
