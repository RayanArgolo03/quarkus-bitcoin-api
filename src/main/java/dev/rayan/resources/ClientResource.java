package dev.rayan.resources;

import dev.rayan.dto.request.client.ClientsByAddressFilterRequest;
import dev.rayan.dto.request.client.ClientsByCreatedAtRequest;
import dev.rayan.dto.request.client.CreateClientRequest;
import dev.rayan.dto.request.client.UpdateClientRequest;
import dev.rayan.dto.response.client.ClientResponse;
import dev.rayan.model.Address;
import dev.rayan.model.Credential;
import dev.rayan.services.AddressService;
import dev.rayan.services.AuthenticationService;
import dev.rayan.services.ClientService;
import dev.rayan.services.KeycloakService;
import io.quarkus.security.Authenticated;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import org.eclipse.microprofile.jwt.Claim;
import org.eclipse.microprofile.jwt.ClaimValue;
import org.eclipse.microprofile.jwt.Claims;
import org.jboss.logging.Logger;

import java.net.URI;
import java.util.UUID;

@Path(ClientResource.RESOUCE_PATH)
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public final class ClientResource {

    public static final String RESOUCE_PATH = "/api/v1/clients";

    @Inject
    ClientService clientService;

    @Inject
    KeycloakService keycloakService;

    @Inject
    AddressService addressService;

    @Inject
    AuthenticationService authenticationService;

    private static final Logger LOG = Logger.getLogger(ClientResource.class);

    @Context
    UriInfo uriInfo;

    @Claim(standard = Claims.sub)
    ClaimValue<String> keycloakUserIdClaim;

    @POST
    @RolesAllowed("user")
    @Transactional(rollbackOn = Exception.class)
    public Response createClient(@Valid @NotNull(message = "Required values!") final CreateClientRequest request) {

        final String keycloakUserId = keycloakUserIdClaim.getValue();

        LOG.info("Finding user email in keycloak");
        final String email = keycloakService.findUserEmailByKeycloakUserId(keycloakUserId);

        LOG.info("Verifyning if is logged in");
        keycloakService.verifyIfLoggedIn(keycloakUserId);

        LOG.info("Getting credential");
        final Credential credential = authenticationService.findCredentialByEmail(email)
                .get();

        LOG.info("Creating client");
        final ClientResponse response = clientService.persist(credential, request);

        LOG.info("Creating uri info");
        final URI uri = uriInfo.getAbsolutePathBuilder()
                .path("id")
                .resolveTemplate("{id}", response.credential().getId())
                .build();

        return Response.created(uri)
                .entity(response)
                .build();
    }

    @PATCH
    @RolesAllowed("user")
    @Transactional(rollbackOn = Exception.class)
    @Path("{id}")
    public Response updateClientPartial(@PathParam("id") @org.hibernate.validator.constraints.UUID(message = "Invalid id!") final UUID id,
                                        @Valid @NotNull(message = "Required values!") final UpdateClientRequest request) {

        final String keycloakUserId = keycloakUserIdClaim.getValue();

        LOG.info("Finding user email in keycloak");
        keycloakService.findUserEmailByKeycloakUserId(keycloakUserId);

        LOG.info("Verifyning if is logged in");
        keycloakService.verifyIfLoggedIn(keycloakUserId);

        LOG.info("Updating client partial");
        clientService.update(id, request);

        return Response.ok()
                .entity("Client updated!")
                .build();
    }

    @PUT
    @RolesAllowed("user")
    @Transactional(rollbackOn = Exception.class)
    @Path("{id}/address")
    public Response updateAddress(@PathParam("id") @org.hibernate.validator.constraints.UUID(message = "Invalid id!") final UUID id,
                                  @Valid @NotNull(message = "Required address") final Address address) {

        final String keycloakUserId = keycloakUserIdClaim.getValue();

        LOG.info("Finding user email in keycloak");
        keycloakService.findUserEmailByKeycloakUserId(keycloakUserId);

        LOG.info("Verifyning if is logged in");
        keycloakService.verifyIfLoggedIn(keycloakUserId);

        LOG.info("Updating client address");
        clientService.update(id, address);

        return Response.ok()
                .entity("Address updated!")
                .build();
    }

    @GET
    @Authenticated
    @Path("/{id}")
    public Response findClientById(@PathParam("id") @org.hibernate.validator.constraints.UUID(message = "Invalid id!") final UUID id) {

        final String keycloakUserId = keycloakUserIdClaim.getValue();

        LOG.info("Finding user email in keycloak");
        keycloakService.findUserEmailByKeycloakUserId(keycloakUserId);

        LOG.info("Verifyning if is logged in");
        keycloakService.verifyIfLoggedIn(keycloakUserId);

        LOG.info("Finding client by id");
        return Response.ok(clientService.findClientById(id))
                .build();
    }

    @GET
    @Authenticated
    @Path("/created-at-period")
    public Response findClientsByCreatedAt(@BeanParam @Valid final ClientsByCreatedAtRequest request) {

        final String keycloakUserId = keycloakUserIdClaim.getValue();

        LOG.info("Finding user email in keycloak");
        keycloakService.findUserEmailByKeycloakUserId(keycloakUserId);

        LOG.info("Verifyning if is logged in");
        keycloakService.verifyIfLoggedIn(keycloakUserId);

        LOG.info("Finding clients by created at period");
        return Response.ok(clientService.findClientsByCreatedAt(request))
                .build();
    }

    @GET
    @Authenticated
    @Path("/address-filter")
    public Response findClientsByAddressFilter(@BeanParam @Valid final ClientsByAddressFilterRequest request) {

        final String keycloakUserId = keycloakUserIdClaim.getValue();

        LOG.info("Finding user email in keycloak");
        keycloakService.findUserEmailByKeycloakUserId(keycloakUserId);

        LOG.info("Verifyning if is logged in");
        keycloakService.verifyIfLoggedIn(keycloakUserId);

        LOG.info("Finding clients by address filter");
        return Response.ok(clientService.findClientsByAddressFilter(request))
                .build();
    }

}
