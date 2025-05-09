package dev.rayan.utils;

import dev.rayan.enums.BaseEnum;
import jakarta.ws.rs.NotFoundException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class EnumConverterUtils {

    public static <T extends Enum<T> & BaseEnum<T>> T convertEnum(final Class<T> enumClass, final String value) {
        return Arrays.stream(enumClass.getEnumConstants())
                .filter(e -> e.getValue().equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Resource not exists!"));
    }

    public static <T extends Enum<T> & BaseEnum<T>> List<T> convertEnums(final Class<T> enumClass, final List<String> values) {
        return (values.isEmpty())
                ? List.of()
                : values.stream()
                .map(value -> convertEnum(enumClass, value))
                .collect(Collectors.toList());
    }

}
