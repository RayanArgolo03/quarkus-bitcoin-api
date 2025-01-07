package dev.rayan.utils;

import dev.rayan.enums.BaseEnum;
import jakarta.ws.rs.NotFoundException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ConverterEnumUtils {

    public static <T extends Enum<T> & BaseEnum<T>> T convertEnum(final Class<T> enumClass, final String value) {

        if (value == null) return null;

        return Arrays.stream(enumClass.getEnumConstants())
                .filter(e -> e.getValue().equals(value))
                .findAny()
                .orElseThrow(() -> new NotFoundException("Resource not found!"));

    }

    public static <T extends Enum<T> & BaseEnum<T>> List<T>  convertEnums(final Class<T> enumClass, final List<String> values) {

        if (values.isEmpty()) return List.of();

        return values.stream()
                .map(value -> convertEnum(enumClass, value))
                .collect(Collectors.toList());

    }

}
