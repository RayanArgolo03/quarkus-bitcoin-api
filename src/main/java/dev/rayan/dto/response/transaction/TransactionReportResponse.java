package dev.rayan.dto.response.transaction;

import lombok.Builder;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

@Builder
public record TransactionReportResponse(

        String transactionsMade,
        BigDecimal totalPurchased,
        String firstPurchase,
        String lastPurchase,
        //0 if null
        BigDecimal totalSold,
        //Can be null if nos has sale transactions
        String firstSold,
        //Can be null if nos has sale transactions
        String lastSold,
        //General attribute
        String lastTransaction
) {
    public Map<String, String> getFieldsAndValues() throws IllegalAccessException {

        //LinkedHashMap preserve original insertion order
        final Map<String, String> fieldsAndValues = new LinkedHashMap<>();
        final Field[] fields = this.getClass().getDeclaredFields();

        for (Field field : fields) {

            field.setAccessible(true);

            fieldsAndValues.put(
                    field.getName(),
                    field.get(this).toString()
            );
        }

        return fieldsAndValues;
    }
}
