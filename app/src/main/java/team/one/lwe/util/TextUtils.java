package team.one.lwe.util;

import org.jetbrains.annotations.NotNull;

public class TextUtils {
    public static boolean isEmpty(String text) {
        return (text == null || text.length() == 0);
    }

    public static boolean isCharDigitOnly(@NotNull String text) {
        return text.matches("[a-zA-Z0-9]+");
    }
}
