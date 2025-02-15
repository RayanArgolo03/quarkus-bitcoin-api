package dev.rayan.dto.request.page;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.ws.rs.QueryParam;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public final class DatePeriodRequest {

    @QueryParam("startDate")
    @NotNull(message = "Required start quotedAt!")
    @PastOrPresent(message = "The start quotedAt can´t be after current quotedAt!")
    LocalDate startDate;

    @QueryParam("endDate")
    @NotNull(message = "Required final quotedAt!")
    @PastOrPresent(message = "The end quotedAt can´t be after current quotedAt!")
    LocalDate endDate;

}
