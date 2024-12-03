package dev.rayan.enums;

import lombok.Getter;

@Getter
public enum TransactionType {

    PURCHASE("Purchase"), SALE("Sale");

    private final String value;

    TransactionType(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
