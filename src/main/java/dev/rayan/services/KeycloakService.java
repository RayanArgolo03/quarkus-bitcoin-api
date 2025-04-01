package dev.rayan.services;

import dev.rayan.dto.response.client.CredentialResponse;
import dev.rayan.dto.response.token.TokensResponse;
import dev.rayan.model.Credential;
import dev.rayan.strategy.TokenStrategy;
import io.quarkus.arc.All;
import io.smallrye.jwt.auth.principal.JWTParser;
import io.smallrye.jwt.auth.principal.ParseException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.ForbiddenException;
import jakarta.ws.rs.NotAuthorizedException;
import jakarta.ws.rs.WebApplicationException;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.eclipse.microprofile.metrics.MetricUnits;
import org.eclipse.microprofile.metrics.annotation.Gauge;
import org.jboss.logging.Logger;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.RolesResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.admin.client.token.TokenManager;
import org.keycloak.representations.AccessTokenResponse;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static jakarta.ws.rs.core.Response.Status.*;
import static java.lang.String.format;

@ApplicationScoped
//@Startup
public final class KeycloakService {

    @ConfigProperty(name = "quarkus.keycloak.admin-client.server-url")
    String serverUrl;

    @ConfigProperty(name = "keycloak.realm")
    String realm;

    @ConfigProperty(name = "keycloak.client-id")
    String clientId;

    @ConfigProperty(name = "keycloak.secret")
    String secret;

    @ConfigProperty(name = "keycloak.admin-username")
    String adminUsername;

    @ConfigProperty(name = "keycloak.admin-password")
    String adminPassword;

    @Inject
    JWTParser jwtParser;

    @Inject
    Logger log;

    @Inject
    @All
    List<TokenStrategy> tokenStrategies;

    private static final String ADMIN_EMAIL = "admin@gmail.com";

    private static final Set<String> QUARKUS_ROLES = Set.of("user", "admin");

    private static final String FIRST_LOGIN_ATTRIBUTE = "first_login";

//    @PostConstruct
//    private void persistMigrationsMock() {
//        log.info("Persisting migrations mock");
//        //Id is null because the keycloak generates a new id in each persist
//        final CredentialResponse response = new CredentialResponse(null, ADMIN_EMAIL, "$2a$10$jkVa9r1OC6uRUhV.sRwGEOMuw4nJQ/doFCB1xM.P8KXYMEd4INCZW", LocalDateTime.now());
//        persist(response, true);
//    }

//    @PreDestroy
//    private void deleteMigrationsMock() {
//
//        log.info("Deleting migrations mock");
//        try (Keycloak keycloak = buildKeycloak(adminUsername, adminPassword)) {
//
//            final UsersResource usersResource = getUsersResource(keycloak);
//            final String keycloakUserId = getUserRepresentation(usersResource, ADMIN_EMAIL)
//                    .getId();
//
//            delete(keycloakUserId);
//        }
//
//    }

    public void persist(final CredentialResponse response, final boolean isEmailVerified) {

        try (Keycloak keycloak = buildKeycloak(adminUsername, adminPassword)) {

            final UserResource user = createUser(getUsersResource(keycloak), response, isEmailVerified);
            assignRolesToUser(keycloak.realm(realm).roles(), user, response.getEmail());

            if (!isEmailVerified) user.sendVerifyEmail();
        }

    }

    private UserResource createUser(final UsersResource usersResource, final CredentialResponse response, final boolean isEmailVerified) {

        final UserRepresentation userRepresentation = new UserRepresentation();

        userRepresentation.setUsername(response.getEmail());
        userRepresentation.setEmail(response.getEmail());
        userRepresentation.setEmailVerified(isEmailVerified);

        final List<CredentialRepresentation> passwordCredential = createUserPassword(response.getPassword());
        userRepresentation.setCredentials(passwordCredential);

        userRepresentation.setEnabled(true);

        userRepresentation.setCreatedTimestamp(response.getCreatedAt()
                .atZone(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli()
        );

        //Set custom first_login attibute to verify in login
        setFirstLoginAttribute(userRepresentation, "true");

        usersResource.create(userRepresentation)
                .close();

        //Get user id in user created above
        final String keycloakUserId = getUserRepresentation(usersResource, response.getEmail())
                .getId();

        response.setKeycloakUserId(keycloakUserId);

        return getUserResource(usersResource, keycloakUserId);
    }


    private List<CredentialRepresentation> createUserPassword(final String password) {

        final CredentialRepresentation credentialRepresentation = new CredentialRepresentation();
        credentialRepresentation.setType(CredentialRepresentation.PASSWORD);
        credentialRepresentation.setValue(password);

        return List.of(credentialRepresentation);
    }

    private void assignRolesToUser(final RolesResource rolesResource, final UserResource user, final String username) {

        final Predicate<RoleRepresentation> isQuarkusRole = role -> QUARKUS_ROLES.contains(role.getName());

        //Filter user and admin roles, sort by name ascending (admin is first pageNumber)
        final List<RoleRepresentation> roles = rolesResource.list()
                .stream()
                .filter(isQuarkusRole)
                .sorted(Comparator.comparing(RoleRepresentation::getName))
                .collect(Collectors.toList());

        //Remove the admin role if not is admin
        if (!username.equals(ADMIN_EMAIL)) roles.remove(0);

        user.roles()
                .realmLevel()
                .add(roles);
    }

    public void resentVerifyEmail(final String keycloakUserId) {

        try (Keycloak keycloak = buildKeycloak(adminUsername, adminPassword)) {

            final UserResource user = getUserResource(getUsersResource(keycloak), keycloakUserId);

            if (isEmailVerified(user.toRepresentation())) {
                throw new WebApplicationException("Email already verified!", GONE);
            }

            user.sendVerifyEmail();
        }

    }

    public TokensResponse login(final CredentialResponse response) {

        try (Keycloak keycloak = buildKeycloak(adminUsername, adminPassword)) {

            final UsersResource usersResource = getUsersResource(keycloak);
            final UserRepresentation userRepresentation = getUserRepresentation(usersResource, response.getEmail());
            final UserResource user = getUserResource(usersResource, userRepresentation.getId());

            if (isFirstLogin(userRepresentation)) {

                if (!isEmailVerified(userRepresentation)) {
                    final String message = "You need to confirm the email %s, verify your email inbox or resend verification email!";
                    throw new WebApplicationException(format(message, response.getEmail()), FORBIDDEN);
                }

                setFirstLoginAttribute(userRepresentation, "false");
                update(user, userRepresentation);

            } else {
                if (hasSession(user)) throw new ForbiddenException("Already logged!");
            }

            return generateTokens(response.getEmail(), response.getPassword());

        }

    }


    public void logout(final String keycloakUserId) {
        try (Keycloak keycloak = buildKeycloak(adminUsername, adminPassword)) {
            getUserResource(getUsersResource(keycloak), keycloakUserId)
                    .logout();
        }
    }

    public TokensResponse generateNewTokens(final String keycloakUserId, final Credential credential) {

        try (Keycloak keycloak = buildKeycloak(adminUsername, adminPassword)) {

            final UsersResource usersResource = getUsersResource(keycloak);

            //Revoking the previous tokens and session
            getUserResource(usersResource, keycloakUserId)
                    .logout();

            return generateTokens(credential.getEmail(), credential.getPassword());
        }
    }


    public String findUserEmailByKeycloakUserId(final String keycloakUserId) throws WebApplicationException {

        try (Keycloak keycloak = buildKeycloak(adminUsername, adminPassword)) {

            return getUserResource(getUsersResource(keycloak), keycloakUserId)
                    .toRepresentation()
                    .getEmail();

        } catch (WebApplicationException e) {
            throw new NotAuthorizedException("Account not exists, you need!", UNAUTHORIZED);
        }
    }

    public void verifyIfLoggedIn(final String keycloakUserId) {

        try (Keycloak keycloak = buildKeycloak(adminUsername, adminPassword)) {
            final UserResource user = getUserResource(getUsersResource(keycloak), keycloakUserId);
            if (!hasSession(user)) throw new ForbiddenException("Desconnected, you need to login again!");
        }
    }

    public String getKeycloakUserIdByRefreshToken(final String refreshToken) throws ParseException {
        final JsonWebToken token = jwtParser.parseOnly(refreshToken);
        tokenStrategies.forEach(strategy -> strategy.validateToken(token));
        return token.getSubject();
    }

    private boolean hasSession(final UserResource user) {
        return !user.getUserSessions().isEmpty();
    }

    private TokensResponse generateTokens(final String email, final String password) {

        try (Keycloak keycloak = buildKeycloak(email, password)) {

            final TokenManager manager = keycloak.tokenManager();
            final AccessTokenResponse accessToken = manager.getAccessToken();

            final LocalDateTime expiresIn = LocalDateTime.ofInstant(
                    Instant.now().plusSeconds(accessToken.getExpiresIn()),
                    ZoneId.systemDefault()
            );

            return new TokensResponse(
                    accessToken.getToken(),
                    accessToken.getRefreshToken(),
                    expiresIn
            );
        }

    }

    private void setFirstLoginAttribute(final UserRepresentation userRepresentation, final String booleanValue) {
        userRepresentation.setAttributes(Map.of(FIRST_LOGIN_ATTRIBUTE, List.of(booleanValue)));
    }

    private UsersResource getUsersResource(final Keycloak keycloak) {
        return keycloak.realm(realm)
                .users();
    }

    private UserResource getUserResource(final UsersResource usersResource, final String keycloakUserId) {
        return usersResource.get(keycloakUserId);
    }


    private UserRepresentation getUserRepresentation(final UsersResource usersResource, final String email) throws IndexOutOfBoundsException {
        return usersResource.search(email)
                .get(0);
    }

    private boolean isFirstLogin(final UserRepresentation userRepresentation) {

        final String firstLoginValue = userRepresentation.getAttributes()
                .get(FIRST_LOGIN_ATTRIBUTE)
                .get(0);

        return firstLoginValue.equals("true");
    }

    private boolean isEmailVerified(final UserRepresentation userRepresentation) {
        return userRepresentation.isEmailVerified();
    }

    public void delete(final String keycloakUserId) {

        try (Keycloak keycloak = buildKeycloak(adminUsername, adminUsername)) {
            getUsersResource(keycloak)
                    .delete(keycloakUserId)
                    .close();
        }
    }

    public void updatePassword(final String email, final String newPassword) {

        try (Keycloak keycloak = buildKeycloak(adminUsername, adminPassword)) {
            final UsersResource usersResource = getUsersResource(keycloak);

            final UserRepresentation userRepresentation = getUserRepresentation(usersResource, email);
            final UserResource user = getUserResource(usersResource, userRepresentation.getId());

            final List<CredentialRepresentation> newPasswordCredential = createUserPassword(newPassword);
            userRepresentation.setCredentials(newPasswordCredential);
            update(user, userRepresentation);
        }

    }

    public void update(final UserResource user, final UserRepresentation userRepresentation) {
        user.update(userRepresentation);
    }


    private Keycloak buildKeycloak(final String username, final String password) {

        final String grantType = (username.equals(adminUsername))
                ? OAuth2Constants.CLIENT_CREDENTIALS
                : OAuth2Constants.PASSWORD;

        return KeycloakBuilder.builder()
                .serverUrl(serverUrl)
                .realm(realm)
                .clientId(clientId)
                .clientSecret(secret)
                .grantType(grantType)
                .username(username)
                .password(password)
                .build();
    }

    @Gauge(
            name = "auth.count.current.users.online",
            absolute = true,
            description = "Count current users online",
            unit = MetricUnits.NONE
    )
    public long countUsersOnline() {

        try (Keycloak keycloak = buildKeycloak(adminUsername, adminPassword)) {

            final List<Map<String, String>> sessionStats = keycloak.realm(realm)
                    .getClientSessionStats();

            return (sessionStats.isEmpty())
                    ? 0L
                    : Long.parseLong(sessionStats.get(0).get("active"));
        }

    }

}



