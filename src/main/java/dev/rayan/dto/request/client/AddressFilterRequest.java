package dev.rayan.dto.request.client;

import jakarta.validation.constraints.Pattern;

public record AddressFilterRequest(

        @Pattern(message = "Only letters!", regexp = "[A-Za-zÀ-ÖØ-öø-ÿ]+$")
        String state,

        @Pattern(message = "Only letters!", regexp = "[A-Za-zÀ-ÖØ-öø-ÿ]+$")
        String street,

        @Pattern(message = "Only letters!", regexp = "[A-Za-zÀ-ÖØ-öø-ÿ]+$")
        String neighbourhood

) {
}
