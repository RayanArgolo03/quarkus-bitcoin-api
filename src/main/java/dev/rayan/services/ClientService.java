package dev.rayan.services;

import dev.rayan.dto.request.ClientRequest;
import dev.rayan.dto.respose.ClientResponse;
import dev.rayan.mappers.ClientMapper;
import dev.rayan.model.client.Client;
import dev.rayan.repositories.ClientRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.NotAuthorizedException;
import jakarta.ws.rs.NotFoundException;

import java.util.Optional;
import java.util.UUID;

import static java.lang.String.format;

@ApplicationScoped
public final class ClientService {

    @Inject
    ClientRepository repository;

    @Inject
    ClientMapper mapper;

    public ClientResponse persist(final ClientRequest request) {

        final Client client = Client.builder().build();
        repository.persist(client);

        return mapper.clientToResponse(client);
    }

    public Client findClientById(final UUID id) {
        final Optional<Client> optional = repository.findByIdOptional(id);
        return optional.orElseThrow(() -> new NotFoundException("You need to complete the register!"));
    }

    public ClientResponse getMappedClient(final Client client) {
        return mapper.clientToResponse(client);
    }
}
