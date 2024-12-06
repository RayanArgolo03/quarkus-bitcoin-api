package dev.rayan.enums;

import dev.rayan.enums.interfaces.BaseEnum;

public enum TransactionType implements BaseEnum<TransactionType> {

    PURCHASE("Purchase"), SALE("Sale");

    private final String value;

    TransactionType(String value) {
        this.value = value;
    }

    @Override
    public String getValue() {return value;}

    @Override
    public String toString() {
        return value;
    }

}
