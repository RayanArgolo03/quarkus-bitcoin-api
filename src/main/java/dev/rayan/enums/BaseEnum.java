package dev.rayan.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public interface BaseEnum<T extends Enum<T>> {
    @JsonValue
    String getValue();
}
