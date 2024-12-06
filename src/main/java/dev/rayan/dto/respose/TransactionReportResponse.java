package dev.rayan.dto.respose;

import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;

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
}
