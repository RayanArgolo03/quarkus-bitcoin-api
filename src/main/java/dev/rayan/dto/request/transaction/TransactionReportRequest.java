package dev.rayan.dto.request.transaction;

import dev.rayan.enums.TransactionReportFormat;
import dev.rayan.enums.TransactionReportPeriod;
import dev.rayan.validation.EnumValidator;
import jakarta.ws.rs.QueryParam;
import lombok.Getter;

@Getter
public final class TransactionReportRequest {

    @QueryParam("period")
    @EnumValidator(enumClass = TransactionReportPeriod.class)
    String period;

    @QueryParam("format")
    @EnumValidator(enumClass = TransactionReportFormat.class)
    String format;


}
