package dev.rayan.services;

import dev.rayan.dto.respose.CredentialResponse;
import dev.rayan.model.client.Credential;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.AllArgsConstructor;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.RolesResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.AccessTokenResponse;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

@ApplicationScoped
public class KeycloakService {

    //Todo refatore

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

    private static final String ADMIN_EMAIL = "rayan@gmail.com";

    //Inner Enum to define keycloak roles
    @AllArgsConstructor
    public enum QuarkusRoles {

        USER("user"), ADMIN("admin");

        private final String value;
    }

    public void persist(final CredentialResponse response) {

        try (Keycloak keycloak = buildKeycloak(adminUsername, adminPassword)) {

            final UsersResource usersResource = keycloak.realm(realm).users();
            final RolesResource rolesResource = keycloak.realm(realm).roles();

            createUser(usersResource, response);
            assignRolesToUser(usersResource, rolesResource, response);
        }
    }

    private void assignRolesToUser(final UsersResource usersResource, final RolesResource rolesResource, final CredentialResponse response) {

        final String userKeycloakId = usersResource.search(response.email())
                .get(0)
                .getId();

        final Predicate<RoleRepresentation> isQuarkusRole = role -> role.getName().equals(QuarkusRoles.USER.value) || role.getName().equals(QuarkusRoles.ADMIN.value);
        final Predicate<RoleRepresentation> isAdminRole = role -> role.getName().equals(QuarkusRoles.ADMIN.value) && response.email().equals(ADMIN_EMAIL);

        final List<RoleRepresentation> roles = rolesResource.list()
                .stream()
                .filter(isQuarkusRole)
                .toList();

        //Remove admin if user not is admin
        roles.removeIf(isAdminRole);

        usersResource.get(userKeycloakId)
                .roles()
                .realmLevel()
                .add(roles);

    }

    private void createUser(final UsersResource usersResource, final CredentialResponse response) {

        final UserRepresentation userRepresentation = new UserRepresentation();

        userRepresentation.setId("asasa");
        userRepresentation.setUsername(response.email());
        userRepresentation.setEmail(response.email());
        userRepresentation.setCredentials(createUserPassword(response.password()));
        userRepresentation.setEnabled(true);

        userRepresentation.setCreatedTimestamp(response.createdAt()
                .atZone(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli()
        );

        usersResource.create(userRepresentation)
                .close();
    }

    private List<CredentialRepresentation> createUserPassword(final String password) {

        final CredentialRepresentation credentialRepresentation = new CredentialRepresentation();

        credentialRepresentation.setType(CredentialRepresentation.PASSWORD);
        credentialRepresentation.setValue(password);
        //If needs modify in the next login credentialRepresentation.setTemporary(true);

        return new ArrayList<>(List.of(credentialRepresentation));
    }

    public String login(final Credential credential) {

        try (Keycloak keycloak = buildKeycloak(credential.getEmail(), credential.getPassword())) {

            AccessTokenResponse token = keycloak.tokenManager().getAccessToken();
            token.setExpiresIn(Instant.now().plus(Duration.ofHours(1)).toEpochMilli());

            return token.getToken();
        }
    }

    private Keycloak buildKeycloak(final String username, final String password) {

        final String grantType = (username.equals("admin"))
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

}



