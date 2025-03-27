package dev.rayan.resources;

import dev.rayan.dto.request.authentication.*;
import dev.rayan.dto.request.token.RefreshTokenRequest;
import dev.rayan.dto.response.client.CredentialResponse;
import dev.rayan.dto.response.client.ForgotPasswordResponse;
import dev.rayan.dto.response.token.TokensResponse;
import dev.rayan.model.Credential;
import dev.rayan.services.AuthenticationService;
import dev.rayan.services.KeycloakService;
import dev.rayan.services.MailerService;
import io.quarkus.security.Authenticated;
import io.smallrye.jwt.auth.principal.ParseException;
import jakarta.annotation.security.PermitAll;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.eclipse.microprofile.metrics.MetricUnits;
import org.eclipse.microprofile.metrics.annotation.Counted;
import org.eclipse.microprofile.metrics.annotation.Metered;
import org.eclipse.microprofile.metrics.annotation.Timed;
import org.jboss.logging.Logger;

import java.net.URI;

import static java.lang.String.format;

@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Path(AuthenticationResource.RESOURCE_PATH)
public final class AuthenticationResource {

    public static final String RESOURCE_PATH = "/api/v1/auth";

    @Inject
    AuthenticationService authenticationService;

    @Inject
    KeycloakService keycloakService;

    @Inject
    MailerService mailerService;

    @Inject
    Logger log;

    @Inject
    UriInfo uriInfo;

    @POST
    @Transactional
    @PermitAll
    @Counted(
            name = "auth.signup.requests.total",
            absolute = true,
            description = "Total request of endpoint create credential"
    )
    @Timed(
            name = "auth.signup.requests.execution.time", absolute = true,
            description = "Execution time of endpoint create credential",
            unit = MetricUnits.SECONDS
    )
    @Metered(
            name = "auth.signup.requests.frequency", absolute = true,
            description = "Frequency of endpoint create credential",
            unit = MetricUnits.MINUTES
    )
    @Path("/sign-up")
    public Response createCredential(@Valid @NotNull(message = "Required values!") final CredentialRequest request) {

        log.info("Persisting client credential in database and keycloak");
        final CredentialResponse response = authenticationService.persistCredential(request);
        keycloakService.persist(response, false);

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
    public Response login(@Valid @NotNull(message = "Required values!") final CredentialRequest request) {

        log.info("Login and generate tokens");
        final TokensResponse response = keycloakService.login(
                authenticationService.login(request)
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

        log.info("Verifyning if is logged in");
        keycloakService.verifyIfLoggedIn(keycloakUserId);

        log.info("Logout user and revoke token");
        keycloakService.logout(keycloakUserId);

        return Response.ok("Sucessfully Logout!")
                .build();
    }

    /**
     * @param request Token can also be defined as @CookieParam
     **/
    @POST
    @PermitAll
    @Path("/generate-new-tokens")
    public Response generateNewTokens(@NotNull(message = "Required refresh token!") final RefreshTokenRequest request) throws ParseException {

        log.info("Getting keycloak user id");
        final String keycloakUserId = keycloakService.getKeycloakUserIdByRefreshToken(
                request.refreshToken()
        );

        log.info("Finding the user email in keycloak");
        final String email = keycloakService.findUserEmailByKeycloakUserId(keycloakUserId);

        log.info("Getting credential to use the email");
        final Credential credential = authenticationService.findCredentialByEmail(email)
                .get();

        log.info("Generate new tokens");
        final TokensResponse response = keycloakService.generateNewTokens(keycloakUserId, credential);

        return Response.ok()
                .entity(response)
                .build();
    }

    @PUT
    @PermitAll
    @Path("{keycloakUserId}/resent-verify-email")
    public Response resentVerifyEmail(@PathParam("keycloakUserId") final String keycloakUserId) {

        log.info("Finding user email in keycloak");
        final String email = keycloakService.findUserEmailByKeycloakUserId(keycloakUserId);

        log.info("Resending verify email");
        keycloakService.resentVerifyEmail(keycloakUserId);

        //Front-end redirect to the login page after confirmation verification
        return Response.accepted()
                .entity(format("Verify email forwarded to %s!", email))
                .build();
    }

    @PATCH
    @PermitAll
    @Transactional
    @Path("/forgot-password")
    public Response sendForgotPasswordEmail(@Valid @NotNull(message = "Required value!") final EmailRequest request) {

        log.info("Persiting the forgot password register in mongodb");
        final ForgotPasswordResponse response = authenticationService.persistForgotPassword(request.email());

        log.info("Sending the email with code and timestamp to expire");
        mailerService.sendForgotPasswordEmail(
                RESOURCE_PATH, request.email(), response.code()
        );

        return Response.accepted()
                .entity(response)
                .build();
    }

    @PATCH
    @PermitAll
    @Transactional
    @Path("/update-forgot-password")
    public Response updateForgotPassword(@BeanParam @Valid final ForgotPasswordRequest forgotRequest,
                                         @Valid @NotNull(message = "Required new passwords!") final NewPasswordRequest newPasswordRequest) {

        log.info("Validating and updating password in the database and keycloak");
        final String encryptedPassword = authenticationService.updateForgotPassword(
                forgotRequest,
                newPasswordRequest.newPassword()
        );
        keycloakService.updatePassword(forgotRequest.getEmail(), encryptedPassword);

        return Response.ok("Password updated!")
                .build();
    }

    @PATCH
    @Authenticated
    @Transactional
    @Path("/update-current-password")
    public Response updateCurrentPassword(@Context final JsonWebToken token,
                                          @Valid @NotNull(message = "Required values!") final UpdatePasswordRequest request) {

        final String keycloakUserId = token.getSubject();

        log.info("Finding user email in keycloak");
        final String email = keycloakService.findUserEmailByKeycloakUserId(keycloakUserId);

        log.info("Verifyning if is logged in");
        keycloakService.verifyIfLoggedIn(keycloakUserId);

        log.info("Validating and updating password in the database and keycloak");
        final String encryptedPassword = authenticationService.updateCurrentPassword(email, request);
        keycloakService.updatePassword(email, encryptedPassword);

        return Response.ok("Password updated!")
                .build();
    }

}
