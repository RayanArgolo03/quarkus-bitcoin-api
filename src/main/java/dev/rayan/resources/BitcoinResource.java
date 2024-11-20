package dev.rayan.resources;

import dev.rayan.dto.respose.BitcoinQuotedResponseDTO;
import dev.rayan.services.BitcoinService;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import org.jboss.logging.Logger;

@Path(BitcoinResource.RESOUCE_PATH)
public final class BitcoinResource {

    public static final String RESOUCE_PATH = "/api/v1/bitcoins";

    @Inject
    Logger log;

    @Inject
    BitcoinService service;

    @GET
    @Path("/quote")
    public Response quote() {

        try {
            log.info("Quoting bitcoin in external API");
            final BitcoinQuotedResponseDTO dto = service.getMappedBitcoin();

            log.info("Quoted!");
            return Response.ok(dto)
                    .build();

        } catch (WebApplicationException e) {

            log.errorf("Server unavailable! %s", e.getMessage());

            return Response.status(e.getResponse().getStatus())
                    .entity("Bitcoin API is down!")
                    .build();
        }

    }

}
