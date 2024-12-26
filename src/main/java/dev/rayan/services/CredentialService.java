package dev.rayan.services;

import dev.rayan.dto.request.CredentialRequest;
import dev.rayan.dto.respose.CredentialResponse;
import dev.rayan.exceptions.BusinessException;
import dev.rayan.mappers.CredentialMapper;
import dev.rayan.model.client.Credential;
import dev.rayan.repositories.CredentialRepository;
import io.quarkus.elytron.security.common.BcryptUtil;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.NotAuthorizedException;

import static java.lang.String.format;

@ApplicationScoped
public final class CredentialService {

    @Inject
    JwtService jwtService;

    @Inject
    CredentialMapper mapper;

    @Inject
    CredentialRepository repository;

    public Credential persist(final CredentialRequest request) {

        if (repository.findCredential(request.email().toLowerCase()).isPresent()) {
            throw new BusinessException(format("Client with email %s already exists!", request.email()));
        }

        final Credential credential = new Credential(request.email(), request.password());
        repository.persist(credential);

        return credential;
    }

    public CredentialResponse login(final CredentialRequest request) {

        //Necessary to display the exception message correctly
        final int statusCode = 401;
        final String message = "Invalid password or email!";

        final Credential credential = repository.findCredential(request.email())
                .orElseThrow(() -> new NotAuthorizedException(message, statusCode));

        if (!BcryptUtil.matches(request.password(), credential.getPassword())) {
            throw new NotAuthorizedException(message, statusCode);
        }

        String token = jwtService.generateToken(credential);
        System.out.println("as-as-a0s-a0 TOKEN HERE "+token);

        return mapper.credentialToResponse(credential, token);
    }


}
