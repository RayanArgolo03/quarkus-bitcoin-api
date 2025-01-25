package dev.rayan.resources;

import dev.rayan.dto.request.CredentialRequest;
import dev.rayan.dto.request.RefreshTokenRequest;
import dev.rayan.dto.respose.CredentialResponse;
import dev.rayan.dto.respose.CredentialTokensResponse;
import dev.rayan.model.client.Credential;
import dev.rayan.services.CredentialService;
import dev.rayan.services.KeycloakService;
import io.quarkus.security.Authenticated;
import io.smallrye.jwt.auth.principal.ParseException;
import jakarta.annotation.security.PermitAll;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.jboss.logging.Logger;
import org.keycloak.representations.idm.UserRepresentation;

import java.net.URI;

import static java.lang.String.format;

@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Path(AuthenticationResource.RESOURCE_PATH)
public final class AuthenticationResource {

    public static final String RESOURCE_PATH = "/api/v1/auth";

    @Inject
    CredentialService credentialService;

    @Inject
    KeycloakService keycloakService;

    @Inject
    Logger log;

    @Inject
    UriInfo uriInfo;

    @POST
    @Transactional
    @PermitAll
    @Path("/sign-up")
    public Response createCredential(@Valid final CredentialRequest request) {

        log.info("Persisting client credential in database and keycloak");
        final CredentialResponse response = credentialService.persist(request);
        keycloakService.persist(response);

        log.info("Creating uri info");
        final URI uri = uriInfo.getAbsolutePathBuilder()
                .path("/{id}")
                .resolveTemplate("id", response.getId())
                .build();

        //Front-end redirect to the login page after confirmation verification
        //The keycloak user id is used in path param to resend the verification email
        return Response.created(uri)
                .entity(format("The confirmation email was sent to: %s \nKeycloak user id: %s", response.getEmail(), response.getKeycloakUserId()))
                .build();
    }

    @POST
    @PermitAll
    @Path("/login")
    public Response login(@Valid final CredentialRequest request) {

        log.info("Login and generate tokens");
        final CredentialTokensResponse response = keycloakService.login(
                credentialService.login(request)
        );

        //Front-end redirect to index-page
        return Response.ok()
                .entity(response)
                .build();
    }

    @DELETE
    @Authenticated
    @Path("/logout")
    public Response logout(@Context final JsonWebToken token) {

        log.info("Logout user");
        keycloakService.logout(token);

        //Front-end redirect to index page
        return Response.ok("Sucessfully Logout!")
                .build();
    }

    @PUT
    @PermitAll
    @Path("{keycloakUserId}/resent-verify-email")
    public Response resentVerifyEmail(@PathParam("keycloakUserId") final String keycloakUserId) {

        //Keycloak user id would be extracted by the URL in keycloak expired email response
        keycloakService.resendVerifyEmail(keycloakUserId);

        //Front-end redirect to the login page after confirmation verification
        return Response.ok()
                .entity("Email forwarded!")
                .build();
    }

    /**
     * @param request Token can also be defined as @CookieParam
     **/
    @GET
    @PermitAll
    @Path("/generate-new-tokens")
    public Response generateNewTokens(@Valid final RefreshTokenRequest request) throws ParseException {

        log.info("Validate and find user in keycloak by refresh token");
        final UserRepresentation userRepresentation = keycloakService.findUserByRefreshToken(request.refreshToken());

        log.info("Find credential in database to get the password and generate new tokens");
        final Credential credential = credentialService.findCredential(userRepresentation.getEmail())
                .get();

        log.info("Generate new tokens and revoke previous tokens");
        final CredentialTokensResponse response = keycloakService.generateNewTokens(
                credential.getEmail(),
                credential.getPassword()
        );

        return Response.ok()
                .entity(response)
                .build();
    }

}
