package team.one.lwe.util;

import org.jetbrains.annotations.NotNull;

public class TextUtils {
    public static boolean isEmpty(String text) {
        return (text == null || text.length() == 0);
    }

    public static boolean isLegalUsername(@NotNull String text) {
        return text.matches("[_a-zA-Z0-9]+");
    }

    public static boolean isLegalPassword(@NotNull String text) {
        return text.matches("[-+=_,.a-zA-Z0-9]+");
    }

    public static int getPasswordComplexity(@NotNull String text) {
        int comp = 0;
        if (text.matches(".*[-+=_,.].*")) comp++;
        if (text.matches(".*[a-zA-Z].*")) comp++;
        if (text.matches(".*[0-9].*")) comp++;
        return comp;
    }
}
