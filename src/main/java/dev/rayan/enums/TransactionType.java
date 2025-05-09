package dev.rayan.enums;

import lombok.Getter;

@Getter
public enum TransactionType implements BaseEnum<TransactionType> {

    PURCHASE("Purchase"), SALE("Sale");

    private final String value;

    TransactionType(String value) {
        this.value = value;
    }

}
