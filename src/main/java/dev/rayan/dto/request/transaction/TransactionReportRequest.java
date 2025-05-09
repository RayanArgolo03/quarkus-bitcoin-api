package dev.rayan.dto.request.transaction;

import dev.rayan.enums.TransactionReportFormat;
import dev.rayan.enums.TransactionReportPeriod;
import dev.rayan.validation.EnumValidator;
import jakarta.ws.rs.FormParam;


public record TransactionReportRequest(

        @FormParam("period")
        @EnumValidator(message = "Invalid period!", enumClass = TransactionReportPeriod.class)
        String period,

        @FormParam("format")
        @EnumValidator(message = "Invalid format!", enumClass = TransactionReportFormat.class)
        String format

) {}