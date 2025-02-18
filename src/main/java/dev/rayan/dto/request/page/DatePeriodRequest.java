package dev.rayan.dto.request.page;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.ws.rs.QueryParam;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public final class DatePeriodRequest {

    @QueryParam("startDate")
    @NotNull(message = "Required start date!")
    @PastOrPresent(message = "The start date can´t be after current date!")
    LocalDate startDate;

    @QueryParam("endDate")
    @NotNull(message = "Required end date!")
    @PastOrPresent(message = "The end date can´t be after current date!")
    LocalDate endDate;

}
