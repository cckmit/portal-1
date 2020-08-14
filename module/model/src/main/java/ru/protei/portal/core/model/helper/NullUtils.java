package ru.protei.portal.core.model.helper;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.Callable;

public class NullUtils {

    public static @NotNull String emptyIfNull(@NotNull Callable<String> block) {
        return defaultIfNull(block, "");
    }

    public static <T> T defaultIfNull(@NotNull Callable<T> block, T def) {
        return defaultIfNull(defaultIfNpe(block, def), def);
    }

    public static <T> T defaultIfNull(T value, T def) {
        return value != null ? value : def;
    }

    public static <T> T defaultIfNpe(@NotNull Callable<T> block, T def) {
        try {
            return block.call();
        } catch (NullPointerException e) {
            return def;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
