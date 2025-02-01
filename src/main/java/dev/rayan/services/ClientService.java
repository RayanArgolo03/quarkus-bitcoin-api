package dev.rayan.services;

import dev.rayan.dto.request.CreateClientRequest;
import dev.rayan.dto.request.UpdateClientRequest;
import dev.rayan.dto.respose.ClientResponse;
import dev.rayan.exceptions.UserAlreadyExistsException;
import dev.rayan.mappers.ClientMapper;
import dev.rayan.model.client.Address;
import dev.rayan.model.client.Client;
import dev.rayan.model.client.Credential;
import dev.rayan.repositories.ClientRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.NotAuthorizedException;

import java.util.UUID;

import static jakarta.ws.rs.core.Response.Status.GONE;
import static jakarta.ws.rs.core.Response.Status.UNAUTHORIZED;

@ApplicationScoped
public final class ClientService {

    @Inject
    ClientRepository repository;

    @Inject
    ClientMapper mapper;

    public ClientResponse persist(final Credential credential, final CreateClientRequest request) {

        //If tryning create client again
        if (repository.findByIdOptional(credential.getId()).isPresent()) {
            throw new UserAlreadyExistsException("Your register is already completed!", GONE);
        }

        if (repository.findCpf(request.cpf()).isPresent()) {
            throw new NotAuthorizedException("CPF already exists!", UNAUTHORIZED);
        }

        final Client client = Client.builder()
                .firstName(request.firstName())
                .surname(request.surname())
                .birthDate(request.birthDate())
                .cpf(request.cpf())
                .credential(credential)
                .address(request.address())
                .build();

        repository.persist(client);

        return mapper.clientToResponse(client);
    }

    public ClientResponse findClientById(final UUID id) {
        return repository.findByIdOptional(id)
                .map(mapper::clientToResponse)
                .orElseThrow(() -> new NotAuthorizedException("Client not exists!", UNAUTHORIZED));

    }

    public void update(final UUID id, final UpdateClientRequest request) {

        final Client client = repository.findByIdOptional(id)
                .orElseThrow(() -> new NotAuthorizedException("You need complete your register!", UNAUTHORIZED));

        repository.updatePartial(client, request);
    }

    public void update(final UUID id, final Address address) {

        final Client client = repository.findByIdOptional(id)
                .orElseThrow(() -> new NotAuthorizedException("You need complete your register!", UNAUTHORIZED));

        repository.updateAddress(client, address);
    }


}

