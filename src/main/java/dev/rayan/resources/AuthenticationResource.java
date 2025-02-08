package dev.rayan.resources;

import dev.rayan.dto.request.client.CreateCredentialRequest;
import dev.rayan.dto.request.token.RefreshTokenRequest;
import dev.rayan.dto.response.token.CredentialResponse;
import dev.rayan.dto.response.token.CredentialTokensResponse;
import dev.rayan.model.Credential;
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

import java.net.URI;

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
    public Response createCredential(@Valid final CreateCredentialRequest request) {

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
                .entity(response)
                .build();
    }

    @POST
    @PermitAll
    @Path("/login")
    public Response login(@Valid final CreateCredentialRequest request) {

        log.info("Login and generate tokens");
        final CredentialTokensResponse response = keycloakService.login(
                credentialService.login(request)
        );

        //Front-end redirect to pageNumber-page
        return Response.ok()
                .entity(response)
                .build();
    }

    @DELETE
    @Authenticated
    @Path("/logout")
    public Response logout(@Context final JsonWebToken token) {

        final String keycloakUserId = token.getSubject();

        log.info("Verifyning if user exists in keycloak");
        keycloakService.findUserEmailByKeycloakUserId(keycloakUserId);

        log.info("Finding the credential to revoke token with email and password");
        final Credential credential = credentialService.findCredential(token.getClaim("email"))
                .get();

        log.info("Logout user and revoke token");
        keycloakService.logout(keycloakUserId);

        //Front-end redirect to pageNumber page
        return Response.ok("Sucessfully Logout!")
                .build();
    }

    /**
     * @param request Token can also be defined as @CookieParam
     **/
    @POST
    @PermitAll
    @Path("/generate-new-tokens")
    public Response generateNewTokens(final RefreshTokenRequest request) throws ParseException {

        log.info("Validating refresh token and get keycloak user id");
        final String keycloakUserId = keycloakService.validateToken(request.refreshToken())
                .getSubject();

        log.info("Finding the user email in keycloak");
        final String email = keycloakService.findUserEmailByKeycloakUserId(keycloakUserId);

        log.info("Finding credential to use the password");
        final Credential credential = credentialService.findCredential(email)
                .get();

        log.info("Generate new tokens");
        final CredentialTokensResponse response = keycloakService.generateNewTokens(keycloakUserId, credential);

        return Response.ok()
                .entity(response)
                .build();
    }


    @PUT
    @PermitAll
    @Path("{keycloakUserId}/resent-verify-email")
    public Response resentVerifyEmail(@PathParam("keycloakUserId") final String keycloakUserId) {

        log.info("Verifyning if credential exists in keycloak");
        keycloakService.findUserEmailByKeycloakUserId(keycloakUserId);

        log.info("Resending verify email");
        keycloakService.resentVerifyEmail(keycloakUserId);

        //Front-end redirect to the login page after confirmation verification
        return Response.ok()
                .entity("Email forwarded!")
                .build();
    }
}
