package dev.rayan.resources;

import dev.rayan.services.BitcoinService;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
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

        log.info("Quoting bitcoin in external API");
        return Response.ok(service.getMappedBitcoin())
                .build();
    }

}
