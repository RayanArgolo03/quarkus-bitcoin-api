package dev.rayan.dto.request;

import dev.rayan.enums.TransactionReportFormat;
import dev.rayan.enums.TransactionReportPeriod;
import jakarta.validation.constraints.NotNull;

public record TransactionReportRequest(
        @NotNull(message = "Period required!") TransactionReportPeriod period,
        @NotNull(message = "Format required!") TransactionReportFormat format
) {
}
