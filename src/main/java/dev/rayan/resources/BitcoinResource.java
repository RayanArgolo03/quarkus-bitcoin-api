package dev.rayan.resources;

import dev.rayan.model.bitcoin.Bitcoin;
import dev.rayan.services.BitcoinService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;
import org.jboss.logging.Logger;

@Path(BitcoinResource.RESOUCE_PATH)
@ApplicationScoped
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
        final Bitcoin bitcoin = service.quote();

        //Use fail first principle
        if (bitcoin == null) {
            log.error("Server unavailable!");

            return Response.status(Response.Status.SERVICE_UNAVAILABLE)
                    .entity("Bitcoin API is down!")
                    .build();
        }

        log.info("Quoted!");
        return Response.ok(service.mapBitcoinQuoted())
                .build();
    }

}
