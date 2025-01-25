package dev.rayan.services;

import dev.rayan.dto.respose.CredentialResponse;
import dev.rayan.dto.respose.CredentialTokensResponse;
import dev.rayan.exceptions.EmailAlreadyVerifiedException;
import dev.rayan.exceptions.EmailNotVerifiedException;
import dev.rayan.exceptions.UserAlreadyLoggedException;
import dev.rayan.exceptions.UserAlreadyLoggedOutException;
import dev.rayan.strategy.TokenStrategy;
import io.quarkus.arc.All;
import io.quarkus.runtime.Startup;
import io.smallrye.jwt.auth.principal.JWTParser;
import io.smallrye.jwt.auth.principal.ParseException;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.RolesResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.admin.client.token.TokenManager;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static jakarta.ws.rs.core.Response.Status.FORBIDDEN;
import static jakarta.ws.rs.core.Response.Status.GONE;
import static java.lang.String.format;

@ApplicationScoped
@Startup
public class KeycloakService {

    @ConfigProperty(name = "keycloak.admin-client.server-url")
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
    @All
    List<TokenStrategy> tokenStrategies;

    private static final String ADMIN_EMAIL = "admin@gmail.com";

    private static final Set<String> QUARKUS_ROLES = Set.of("user", "admin");

    private static final String FIRST_LOGIN_ATTRIBUTE = "first_login";

    @PostConstruct
    public void persistMigrationsMock() {
        final CredentialResponse mock = new CredentialResponse(null, "rayanpetros2@gmail.com", "$2a$10$SfSv2jWTsyMSS0zk0/yVL.UtLF7g1HKiaQG0kBYHh0FTLIpyPsDeq", LocalDateTime.now());
        mock.setKeycloakUserId("8c878e6f-ff13-4a37-a208-7510c2638944");
        persist(mock);
    }

    @PreDestroy
    public void deleteMigrationsMock() {
        final CredentialResponse mock = new CredentialResponse(null, "rayanpetros2@gmail.com", "$2a$10$SfSv2jWTsyMSS0zk0/yVL.UtLF7g1HKiaQG0kBYHh0FTLIpyPsDeq", LocalDateTime.now());
        mock.setKeycloakUserId("8c878e6f-ff13-4a37-a208-7510c2638944");
        delete(mock.getKeycloakUserId());
    }

    public void persist(final CredentialResponse response) {

        final Keycloak keycloak = buildKeycloak(adminUsername, adminPassword);
        final UserResource user = createUser(getUsersResource(keycloak), response);

        assignRolesToUser(keycloak.realm(realm).roles(), user, response.getEmail());
        user.sendVerifyEmail();

        closeKeycloak(keycloak);

    }

    private UserResource createUser(final UsersResource usersResource, final CredentialResponse response) {

        final UserRepresentation user = new UserRepresentation();

        user.setUsername(response.getEmail());
        user.setEmail(response.getEmail());
        user.setCredentials(createUserPassword(response.getPassword()));
        user.setEnabled(true);

        user.setCreatedTimestamp(response.getCreatedAt()
                .atZone(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli()
        );

        //Set custom first_login attibute to verify in login
        user.setAttributes(
                Map.of(FIRST_LOGIN_ATTRIBUTE, List.of("true"))
        );

        usersResource.create(user)
                .close();

        //Get user id in user created above
        final String keycloakUserId = usersResource.search(response.getEmail())
                .get(0)
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

        //Filter user and admin roles, sort by name ascending (admin is first index)
        final List<RoleRepresentation> roles = rolesResource.list()
                .stream()
                .filter(isQuarkusRole)
                .sorted(Comparator.comparing(RoleRepresentation::getName))
                .collect(Collectors.toList());

        //Remove the admin role if not is admin
        if (username.equals(ADMIN_EMAIL)) roles.remove(0);

        user.roles()
                .realmLevel()
                .add(roles);
    }

    public void resendVerifyEmail(final String keycloakUserId) {

        final Keycloak keycloak = buildKeycloak(adminUsername, adminPassword);
        final UserResource user = getUserResource(getUsersResource(keycloak), keycloakUserId);

        if (user.toRepresentation().isEmailVerified()) {
            throw new EmailAlreadyVerifiedException("Email already verified!", GONE);
        }

        user.sendVerifyEmail();
        closeKeycloak(keycloak);
    }


    public CredentialTokensResponse login(final CredentialResponse response) {

        Keycloak keycloak = buildKeycloak(adminUsername, adminPassword);

        final UsersResource usersResource = getUsersResource(keycloak);

        final UserRepresentation userRepresentation = usersResource
                .search(response.getEmail())
                .get(0);

        final UserResource user = getUserResource(usersResource, userRepresentation.getId());

        if (isFirstLogin(userRepresentation)) {

            //Front-end redirect to the confirmation email
            if (!userRepresentation.isEmailVerified()) {
                user.sendVerifyEmail();
                throw new EmailNotVerifiedException(format("You need to confirm the email %s, verify your email inbox!", response.getEmail()), FORBIDDEN);
            }

            userRepresentation.setAttributes(Map.of(FIRST_LOGIN_ATTRIBUTE, List.of("false")));
            updateUser(user, userRepresentation);

        } else {
            //Front-end redirect to index page
            if (hasSession(user)) throw new UserAlreadyLoggedException("Already logged!", FORBIDDEN);
        }

        keycloak = buildKeycloak(response.getEmail(), response.getPassword());
        final CredentialTokensResponse tokensResponse = generateTokens(keycloak);
        closeKeycloak(keycloak);

        return tokensResponse;
    }


    public void logout(final JsonWebToken token) {

        final Keycloak keycloak = buildKeycloak(adminUsername, adminPassword);

        final UserResource user = getUserResource(getUsersResource(keycloak), token.getSubject());
        if (!hasSession(user)) throw new UserAlreadyLoggedOutException("Already logged out!", GONE);

        user.logout();

        closeKeycloak(keycloak);
    }

    public CredentialTokensResponse generateNewTokens(final String email, final String password) {

        Keycloak keycloak = buildKeycloak(adminUsername, adminPassword);

        final UsersResource usersResource = getUsersResource(keycloak);
        final String keycloakUserId = usersResource.search(email)
                .get(0)
                .getId();

        //Revoking the previous tokens and session
        getUserResource(usersResource, keycloakUserId)
                .logout();

        keycloak = buildKeycloak(email, password);
        final CredentialTokensResponse tokensResponse = generateTokens(keycloak);

        closeKeycloak(keycloak);

        return tokensResponse;
    }

    public UserRepresentation findUserByRefreshToken(final String refreshToken) throws ParseException {

        final JsonWebToken token = jwtParser.parseOnly(refreshToken);
        tokenStrategies.forEach(strategy -> strategy.validateToken(token));

        final Keycloak keycloak = buildKeycloak(adminUsername, adminPassword);

        final UserRepresentation userRepresentation = getUserResource(getUsersResource(keycloak), token.getSubject())
                .toRepresentation();

        closeKeycloak(keycloak);

        return userRepresentation;
    }

    private boolean hasSession(final UserResource user) {
        return !user.getUserSessions().isEmpty();
    }

    public CredentialTokensResponse generateTokens(final Keycloak keycloak) {
        final TokenManager manager = keycloak.tokenManager();
        return new CredentialTokensResponse(manager.getAccessTokenString(), manager.refreshToken().getRefreshToken());
    }


    private UsersResource getUsersResource(final Keycloak keycloak) {
        return keycloak.realm(realm)
                .users();
    }

    private UserResource getUserResource(final UsersResource usersResource, final String keycloakUserId) {
        return usersResource.get(keycloakUserId);
    }

    private boolean isFirstLogin(final UserRepresentation userRepresentation) {

        final String firstLoginValue = userRepresentation.getAttributes()
                .get(FIRST_LOGIN_ATTRIBUTE)
                .get(0);

        return firstLoginValue.equals("true");
    }

    public void delete(final String keycloakUserId) {

        final Keycloak keycloak = buildKeycloak(adminUsername, adminUsername);

        getUsersResource(keycloak)
                .delete(keycloakUserId)
                .close();

        closeKeycloak(keycloak);
    }

    public void updateUser(final UserResource user, final UserRepresentation userRepresentation) {
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

    private void closeKeycloak(final Keycloak keycloak) {
        keycloak.close();
    }
}



