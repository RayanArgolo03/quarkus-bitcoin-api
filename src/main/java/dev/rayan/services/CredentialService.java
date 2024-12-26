package dev.rayan.services;

import dev.rayan.dto.request.CredentialRequest;
import dev.rayan.exceptions.BusinessException;
import dev.rayan.model.client.Credential;
import dev.rayan.repositories.CredentialRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import static java.lang.String.format;

@ApplicationScoped
public final class CredentialService {

    @Inject
    CredentialRepository repository;

    public Credential persist(final CredentialRequest request) {

        if (repository.findCredential(request).isPresent()) {
            throw new BusinessException(format("Client with email %s already exists!", request.email()));
        }

        final Credential credential = new Credential(request.email(), request.password());
        repository.persist(credential);

        return credential;
    }

}
