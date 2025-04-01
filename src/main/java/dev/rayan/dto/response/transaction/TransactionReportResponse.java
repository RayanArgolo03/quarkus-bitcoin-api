package dev.rayan.dto.response.transaction;

import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

@Getter
public final class TransactionReportResponse {

    String transactionsMade;

    BigDecimal totalPurchased;
    String firstPurchase;
    String lastPurchase;

    //0 if null
    BigDecimal totalSold;
    //Can be null if nos has sale transactions
    String firstSold;
    //Can be null if nos has sale transactions
    String lastSold;

    //General attribute
    String lastTransaction;

    //Bitcoin attribute
    @Setter
    String bitcoinCurrentValue;


    //Serialize JSON to Java constructor
    public TransactionReportResponse(String transactionsMade, BigDecimal totalPurchased, String firstPurchase, String lastPurchase, BigDecimal totalSold, String firstSold, String lastSold, String lastTransaction) {
        this.transactionsMade = transactionsMade;
        this.totalPurchased = totalPurchased;
        this.firstPurchase = firstPurchase;
        this.lastPurchase = lastPurchase;
        this.totalSold = totalSold;
        this.firstSold = firstSold;
        this.lastSold = lastSold;
        this.lastTransaction = lastTransaction;
    }

    public Map<String, String> getFieldsAndValues() throws IllegalAccessException {

        //LinkedHashMap preserve original insertion order
        final Map<String, String> fieldsAndValues = new LinkedHashMap<>();

        for (Field field : this.getClass().getDeclaredFields()) {

            field.setAccessible(true);

            fieldsAndValues.put(
                    field.getName(),
                    field.get(this).toString()
            );
        }

        return fieldsAndValues;
    }
}
