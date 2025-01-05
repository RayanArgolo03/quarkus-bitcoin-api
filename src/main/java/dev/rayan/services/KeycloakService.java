package dev.rayan.services;

import dev.rayan.model.client.Credential;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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
    String username;

    @ConfigProperty(name = "keycloak.password")
    String password;

    @ConfigProperty(name = "keycloak.grant-type")
    String grantType;

    Keycloak keycloak;

    @PostConstruct
    public void initKeycloak() {
        keycloak = KeycloakBuilder.builder()
                .serverUrl(serverUrl)
                .realm(realm)
                .clientId(clientId)
                .clientSecret(secret)
                .grantType(grantType)
                .username(username)
                .password(password)
                .build();
    }

    @PreDestroy
    public void closeKeycloak() {
        keycloak.close();
    }

    public void persistCredential(final Credential credential) {
        keycloak.realm(realm)
                .users()
                .create(createUserRepresentation(credential))
                .close();
    }

    private UserRepresentation createUserRepresentation(final Credential credential) {

        final UserRepresentation userRepresentation = new UserRepresentation();

        userRepresentation.setId(credential.getId().toString());
        userRepresentation.setUsername(credential.getEmail());
        userRepresentation.setEmail(credential.getEmail());
        userRepresentation.setCredentials(createUserPassword(credential.getPassword()));
        userRepresentation.setEnabled(true);
        userRepresentation.setCreatedTimestamp(credential.getCreatedAt().toEpochSecond());
        // Use after email verifyning userRepresentation.setEmailVerified();
        userRepresentation.setRealmRoles(getRealmRoles(credential.getEmail()));

        return userRepresentation;
    }

    private List<CredentialRepresentation> createUserPassword(final String password) {

        final CredentialRepresentation credentialRepresentation = new CredentialRepresentation();

        credentialRepresentation.setType(CredentialRepresentation.PASSWORD);
        credentialRepresentation.setValue(password);
        //If needs modify in the next login credentialRepresentation.setTemporary(true);

        return new ArrayList<>(List.of(credentialRepresentation));
    }

    private List<String> getRealmRoles(final String email) {
        return (email.equals("rayan@admin"))
                ? List.of("user", "admin")
                : List.of("user");
    }


}



