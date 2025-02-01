package dev.rayan.resources;

import dev.rayan.services.BitcoinService;
import io.quarkus.security.Authenticated;
import io.quarkus.security.jpa.Roles;
import jakarta.annotation.security.DenyAll;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import org.jboss.logging.Logger;

@Path(BitcoinResource.RESOUCE_PATH)
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public final class BitcoinResource {

    public static final String RESOUCE_PATH = "/api/v1/bitcoins";

    @Inject
    Logger log;

    @Inject
    BitcoinService service;

    @GET
    @Authenticated
    public Response quote() {
        log.info("Quoting bitcoin in external API");
        return Response.ok(service.getMappedBitcoin())
                .build();
    }

}
