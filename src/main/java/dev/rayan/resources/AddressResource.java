package dev.rayan.resources;

import dev.rayan.services.AddressService;
import dev.rayan.services.KeycloakService;
import io.quarkus.security.Authenticated;
import jakarta.inject.Inject;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.jwt.Claim;
import org.eclipse.microprofile.jwt.ClaimValue;
import org.eclipse.microprofile.jwt.Claims;
import org.jboss.logging.Logger;

@Path(AddressResource.RESOUCE_PATH)
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public final class AddressResource {

    public static final String RESOUCE_PATH = "/api/v1/addresses";

    @Inject
    AddressService addressService;

    @Inject
    KeycloakService keycloakService;

    @Claim(standard = Claims.sub)
    ClaimValue<String> keycloakUserIdClaim;

    private static final Logger LOG = Logger.getLogger(AddressResource.class);

    @GET
    @Authenticated
    @Path("/{cep}")
    public Response findAddressByCep(@PathParam("cep")
                                     @NotBlank(message = "Required CEP!")
                                     @Pattern(regexp = "^\\d{8}$", message = "The CEP should have strictly 8 numbers!") final String cep) {

        final String keycloakUserId = keycloakUserIdClaim.getValue();

        LOG.info("Verifyning if user exists in keycloak");
        keycloakService.findUserEmailByKeycloakUserId(keycloakUserId);

        LOG.info("Verifyning if is logged in");
        keycloakService.verifyIfLoggedIn(keycloakUserId);

        LOG.info("Finding address by cep");
        return Response.ok()
                .entity(addressService.findAddressByCep(cep))
                .build();
    }

}
