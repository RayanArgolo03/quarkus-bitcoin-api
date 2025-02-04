package dev.rayan.resources;

import dev.rayan.dto.request.client.ClientsByCreatedAtRequest;
import dev.rayan.dto.request.client.ClientsByStateRequest;
import dev.rayan.dto.request.client.CreateClientRequest;
import dev.rayan.dto.request.client.UpdateClientRequest;
import dev.rayan.dto.response.client.CreatedClientResponse;
import dev.rayan.model.client.Address;
import dev.rayan.model.client.Credential;
import dev.rayan.services.ClientService;
import dev.rayan.services.CredentialService;
import dev.rayan.services.KeycloakService;
import io.quarkus.security.Authenticated;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.hibernate.query.Page;
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
    CredentialService credentialService;

    @Inject
    Logger log;

    @Context
    UriInfo uriInfo;

    @Context
    JsonWebToken token;

    @GET
    @PermitAll
    @Path("/index-page")
    public Response index() {
        return Response.ok("Bitcoin Exchange by Rayan :)")
                .build();
    }

    @POST
    @RolesAllowed("user")
    @Transactional
    public Response createClient(@Valid final CreateClientRequest request) {

        log.info("Verifyning if credential exists in keycloak");
        keycloakService.findUserEmailByKeycloakUserId(token.getSubject());

        log.info("Getting credential");
        final Credential credential = credentialService.findCredential(token.getClaim("email"))
                .get();

        log.info("Creating client");
        final CreatedClientResponse response = clientService.persist(credential, request);

        log.info("Creating uri info");
        final URI uri = uriInfo.getAbsolutePathBuilder()
                .path("id")
                .resolveTemplate("{id}", response.id())
                .build();

        return Response.created(uri)
                .entity(response)
                .build();
    }

    @PATCH
    @RolesAllowed("user")
    @Transactional
    @Path("{id}")
    public Response updatePartial(@PathParam("id") final UUID id,
                                  @Valid final UpdateClientRequest request) {

        log.info("Verifyning if credential exists in keycloak");
        keycloakService.findUserEmailByKeycloakUserId(token.getSubject());

        log.info("Updating client partial");
        clientService.update(id, request);

        return Response.ok()
                .entity("Client updated!")
                .build();
    }

    @PUT
    @RolesAllowed("user")
    @Transactional
    @Path("{id}")
    public Response updateAddress(@PathParam("id") final UUID id,
                                  @Valid final Address address) {

        log.info("Verifyning if credential exists in keycloak");
        keycloakService.findUserEmailByKeycloakUserId(token.getSubject());

        log.info("Updating client address");
        clientService.update(id, address);

        return Response.ok()
                .entity("Address updated!")
                .build();
    }


    @GET
    @Authenticated
    @Path("/{id}")
    public Response findClientById(@PathParam("id")
                                   @NotNull(message = "Required id!") final UUID id) {

        log.info("Finding client by id");
        return Response.ok(clientService.findClientById(id))
                .build();
    }

    @GET
    @Authenticated
    @Path("/created-at-period")
    public Response findClientsByCreatedAt(@BeanParam @Valid final ClientsByCreatedAtRequest request) {

        log.info("Finding clients by created at period");
        return Response.ok(clientService.findClientsByCreatedAt(request))
                .build();
    }


    @GET
    @Authenticated
    @Path("/by-state")
    public Response findClientsByState(@BeanParam @Valid final ClientsByStateRequest request) {

        log.info("Finding clients by created at period");
        return Response.ok(clientService.findClientsByState(request))
                .build();
    }

}
