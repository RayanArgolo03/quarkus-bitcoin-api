package dev.rayan.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class StringToLowerUtils {

    public static List<String> toLower(final Collection<String> elements) {
        return elements.stream()
                .map(String::toLowerCase)
                .collect(Collectors.toList());
    }

}
