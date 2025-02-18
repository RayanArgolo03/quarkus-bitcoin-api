package dev.rayan.enums;


import com.fasterxml.jackson.annotation.JsonValue;

public enum TransactionType implements BaseEnum<TransactionType> {

    PURCHASE("Purchase"), SALE("Sale");

    private final String value;

    TransactionType(String value) {
        this.value = value;
    }

    @Override
    @JsonValue
    public String getValue() {
        return value;
    }

}
