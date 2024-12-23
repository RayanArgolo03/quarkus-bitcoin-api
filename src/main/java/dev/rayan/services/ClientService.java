package dev.rayan.services;

import dev.rayan.dto.request.ClientRequest;
import dev.rayan.dto.respose.ClientResponse;
import dev.rayan.mappers.ClientMapper;
import dev.rayan.model.client.Client;
import dev.rayan.repositories.ClientRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.SecurityContext;

import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public final class ClientService {

    @Inject
    ClientRepository repository;

    @Inject
    ClientMapper mapper;

    public boolean validClient(final SecurityContext context, final UUID clientId) {

        if (context != null) {
            final Client clientFound = repository.find("username", context.getUserPrincipal().getName())
                    .singleResult();

            return clientFound.getId().equals(clientId);
        }

        return false;
    }

    public Client persistClient(final ClientRequest request) {

        final Client client = Client.builder().build();
        Client.setRoleAndEncryptPassword(client);

        repository.persist(client);

        return client;
    }

    public Client findById(final UUID id) {
        final Optional<Client> optional = repository.findByIdOptional(id);
        return optional.orElseThrow(() -> new NotFoundException("Client not found!"));
    }

    public ClientResponse getMappedClient(final Client client) {
        return mapper.clientToResponse(client);
    }
}
