package dev.rayan.enums.interfaces;

public interface BaseEnum<T extends Enum<T>> {
    String getValue();
}
