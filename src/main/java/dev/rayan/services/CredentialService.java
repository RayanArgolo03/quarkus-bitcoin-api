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
import jakarta.ws.rs.core.Response;

import java.util.Optional;

import static jakarta.ws.rs.core.Response.Status.UNAUTHORIZED;
import static java.lang.String.format;

@ApplicationScoped
public final class CredentialService {

    @Inject
    CredentialRepository repository;

    @Inject
    CredentialMapper mapper;

    public CredentialResponse persist(final CredentialRequest request) {

        if (findCredential(request.email()).isPresent()) {
            throw new NotAuthorizedException(format("Client with email %s already exists!", request.email()), UNAUTHORIZED);
        }

        final Credential credential = new Credential(request.email(), request.password());
        repository.persist(credential);

        return mapper.credentialToResponse(credential);
    }

    public CredentialResponse login(final CredentialRequest request) {

        final Credential credential = findCredential(request.email())
                .orElseThrow(() -> new NotAuthorizedException("Invalid email!", UNAUTHORIZED));

        if (!BcryptUtil.matches(request.password(), credential.getPassword())) {
            throw new NotAuthorizedException("Invalid password", UNAUTHORIZED);
        }

        return mapper.credentialToResponse(credential);
    }

    public Optional<Credential> findCredential(final String email) {
        return repository.findCredential(email.toLowerCase());
    }

}
