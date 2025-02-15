package dev.rayan.dto.request.client;
import jakarta.validation.constraints.NotEmpty;
import jakarta.ws.rs.QueryParam;
import java.util.List;
import java.util.Set;

public record AddressFilterRequest(

        @QueryParam("state")
        Set<@NotEmpty(message = MESSAGE) String> states,

        @QueryParam("street")
        Set<@NotEmpty(message = MESSAGE) String> streets,

        @QueryParam("neighbourhood")
        Set<@NotEmpty(message = MESSAGE) String> neighbourhoods

) {
    private final static String MESSAGE = "Value can´t be not empty!";
}
