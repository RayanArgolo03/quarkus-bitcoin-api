package dev.rayan.dto.request.page;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.ws.rs.QueryParam;

import java.time.LocalDate;

public record DatePeriodRequest(

        @NotNull(message = "Required start date!")
        @QueryParam("startDate")
        @PastOrPresent(message = "The start date can´t be after current date!")
        LocalDate startDate,

        @NotNull(message = "Required end date!")
        @QueryParam("endDate")
        @PastOrPresent(message = "The end date can´t be after current date!")
        LocalDate endDate
) {
}
