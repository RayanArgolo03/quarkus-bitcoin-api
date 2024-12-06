package dev.rayan.dto.request;

import dev.rayan.enums.TransactionReportFormat;
import dev.rayan.enums.TransactionReportPeriod;
import dev.rayan.enums.validation.EnumValidator;
import dev.rayan.model.client.Client;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.QueryParam;
import org.jboss.resteasy.reactive.RestQuery;

import java.math.BigDecimal;

public record TransactionReportRequest() {
}
