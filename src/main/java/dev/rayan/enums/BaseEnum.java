package dev.rayan.enums;

public interface BaseEnum<T extends Enum<T>> {
    String getValue();
}
