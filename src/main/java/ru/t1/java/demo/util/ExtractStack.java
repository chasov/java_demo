package ru.t1.java.demo.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import static java.util.Objects.requireNonNull;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ExtractStack {

    public static String getStackTraceAsString(Throwable ex) {
        requireNonNull(ex, "ex is null");
        StringBuilder result = new StringBuilder();
        for (StackTraceElement element : ex.getStackTrace()) {
            result.append(element.toString()).append("\n");
        }
        return result.toString();
    }
}
