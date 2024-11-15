package dev.rayan.resources;

import dev.rayan.dto.respose.BitcoinQuoteResponse;
import dev.rayan.services.BitcoinService;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;
import org.jboss.logging.Logger;

@Path("/api/v1/bitcoins")
public final class BitcoinResource {

    @Inject
    Logger log;

    @Inject
    BitcoinService service;

    @GET
    @Path("/quote")
    public Response quote() {

        log.info("Quoting bitcoin in external API");
        final BitcoinQuoteResponse response = service.quoteBitcoin();

        //Use fail first principle
        if (response == null) {
            log.error("Server unavailable!");

            return Response.status(Response.Status.SERVICE_UNAVAILABLE)
                    .entity("Bitcoin API is down!")
                    .build();
        }

        log.info("Quoted!");
        return Response.ok(response)
                .build();
    }

}
