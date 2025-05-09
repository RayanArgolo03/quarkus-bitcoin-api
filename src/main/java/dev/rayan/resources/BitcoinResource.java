package dev.rayan.resources;

import dev.rayan.services.BitcoinService;
import dev.rayan.services.KeycloakService;
import io.quarkus.security.Authenticated;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.jwt.Claim;
import org.eclipse.microprofile.jwt.ClaimValue;
import org.eclipse.microprofile.jwt.Claims;
import org.jboss.logging.Logger;

@Path(BitcoinResource.RESOUCE_PATH)
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public final class BitcoinResource {

    public static final String RESOUCE_PATH = "/api/v1/bitcoins";

    private static final Logger LOG = Logger.getLogger(ClientResource.class);

    @Inject
    BitcoinService bitcoinService;

    @Inject
    KeycloakService keycloakService;

    @Claim(standard = Claims.sub)
    ClaimValue<String> keycloakUserIdClaim;

    @GET
    @Authenticated
    @Path("/quote")
    public Response quote() {

        final String keycloakUserId = keycloakUserIdClaim.getValue();

        LOG.info("Verifyning if user exists in keycloak");
        final String email = keycloakService.findUserEmailByKeycloakUserId(keycloakUserId);

        LOG.info("Verifyning if is logged in");
        keycloakService.verifyIfLoggedIn(keycloakUserId);

        LOG.info("Quoting bitcoin in external API");
        return Response.ok(bitcoinService.quote())
                .build();
    }

//    @GET
//    @PermitAll
//    @Path("/current-price")
//    public double getCurrentPrice() {
//        log.info("Quoting bitcoin in external API");
//        return bitcoinService.getCurrentPrice();
//    }

}
