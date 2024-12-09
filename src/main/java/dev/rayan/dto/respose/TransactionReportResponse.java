package dev.rayan.dto.respose;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;

import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.Map;

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
@Getter
public final class TransactionReportResponse {

    String transactionsMade;

    //Purchase attributes
    String totalPurchased;
    @Setter
    @NonFinal
    String valuePurchased;
    String firstPurchase;
    String lastPurchase;

    //Sale attributes
    String totalSold;
    @Setter
    @NonFinal
    String valueSold;
    String firstSold;
    String lastSold;
    String lastTransaction;

    //Bitcoin quoting date
    @Setter
    @NonFinal
    String bitcoinDate;

    //Declaring exception in assinature because the compiler not capture exception in stream
    public Map<String, String> getFieldsAndValues() throws IllegalAccessException{

        //LinkedHashMap preserve original insertion order
        final Map<String, String> fieldsAndValues = new LinkedHashMap<>();

        for (Field field : this.getClass().getDeclaredFields()) {
            field.setAccessible(true);

            fieldsAndValues.put(
                    field.getName(),
                    (String) field.get(this)
            );
        }

        return fieldsAndValues;
    }
}
