package dev.rayan.enums;

public enum TransactionType implements BaseEnum<TransactionType> {

    PURCHASE("Purchase"), SALE("Sale");

    private final String value;

    TransactionType(String value) {
        this.value = value;
    }

    @Override
    public String getValue() {
        return value;
    }
}
