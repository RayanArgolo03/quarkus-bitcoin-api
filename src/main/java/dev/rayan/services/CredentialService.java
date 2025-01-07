package dev.rayan.services;

import dev.rayan.dto.request.CredentialRequest;
import dev.rayan.dto.respose.CredentialResponse;
import dev.rayan.mappers.CredentialMapper;
import dev.rayan.model.client.Credential;
import dev.rayan.repositories.CredentialRepository;
import io.quarkus.elytron.security.common.BcryptUtil;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.NotAuthorizedException;
import org.mapstruct.ap.internal.util.IgnoreJRERequirement;

import static java.lang.String.format;

@ApplicationScoped
public final class CredentialService {

    @Inject
    CredentialRepository repository;

    @Inject
    CredentialMapper mapper;

    public CredentialResponse persist(final CredentialRequest request) {

        final int statusCode = 401;

        if (repository.findCredential(request.email().toLowerCase()).isPresent()) {
            throw new NotAuthorizedException(format("Client with email %s already exists!", request.email()), statusCode);
        }

        final Credential credential = new Credential(request.email(), request.password());
        repository.persist(credential);

        return mapper.credentialToResponse(credential);
    }

    public Credential login(final CredentialRequest request) {

        //Necessary to display the exception message correctly
        final int statusCode = 401;
        final String message = "Invalid password or email!";

        final Credential credential = repository.findCredential(request.email())
                .orElseThrow(() -> new NotAuthorizedException(message, statusCode));

        if (!BcryptUtil.matches(request.password(), credential.getPassword())) {
            throw new NotAuthorizedException(message, statusCode);
        }

        return credential;
    }


}
