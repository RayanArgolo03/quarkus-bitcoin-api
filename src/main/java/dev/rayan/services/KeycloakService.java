package dev.rayan.services;

import dev.rayan.model.client.Credential;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
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

@ApplicationScoped
public class KeycloakService {

    @ConfigProperty(name = "keycloak.admin-client.server-url")
    String serverUrl;

    @ConfigProperty(name = "keycloak.realm")
    String realm;

    @ConfigProperty(name = "keycloak.client-id")
    String clientId;

    @ConfigProperty(name = "keycloak.secret")
    String secret;

    @ConfigProperty(name = "keycloak.username")
    String adminUsername;

    @ConfigProperty(name = "keycloak.password")
    String adminPassword;

    public void persist(final Credential credential) {

        try (Keycloak keycloak = buildKeycloak(adminUsername, adminPassword)) {

            final UsersResource users = keycloak.realm(realm).users();
            users.create(createUserRepresentation(credential)).close();

//            assignRolesToUser(users, credential);
        }
    }

    private void assignRolesToUser(final UsersResource users, final Credential credential) {
        users.get(credential.getId().toString())
                .roles()
                .realmLevel()
                .add(List.of(new RoleRepresentation("admin", null, false)));
    }

    private UserRepresentation createUserRepresentation(final Credential credential) {

        final UserRepresentation userRepresentation = new UserRepresentation();

        userRepresentation.setId(credential.getId().toString());
        userRepresentation.setUsername(credential.getEmail());
        userRepresentation.setEmail(credential.getEmail());
        userRepresentation.setCredentials(createUserPassword(credential.getPassword()));
        userRepresentation.setEnabled(true);

        userRepresentation.setCreatedTimestamp(credential.getCreatedAt()
                .atZone(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli()
        );

        return userRepresentation;
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



