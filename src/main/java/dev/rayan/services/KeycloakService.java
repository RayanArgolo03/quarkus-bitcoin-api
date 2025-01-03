package dev.rayan.services;

import dev.rayan.model.client.Credential;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;

import java.util.ArrayList;
import java.util.List;

@RequestScoped
public class KeycloakService {

    @Inject
    Keycloak keycloak;

    @ConfigProperty(name = "quarkus.keycloak.admin-client.realm")
    String realm;

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

    private List<String> getRealmRoles(final String email) {
        return (email.equals("rayan@admin"))
                ? List.of("user", "admin")
                : List.of("user");
    }

    private List<CredentialRepresentation> createUserPassword(final String password) {

        final CredentialRepresentation credentialRepresentation = new CredentialRepresentation();

        credentialRepresentation.setType(CredentialRepresentation.PASSWORD);
        credentialRepresentation.setValue(password);
        //If needs modify in the next login credentialRepresentation.setTemporary(false);

        return new ArrayList<>(List.of(credentialRepresentation));
    }


}
