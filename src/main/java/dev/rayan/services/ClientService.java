package dev.rayan.services;

import dev.rayan.dto.request.client.ClientsByCreatedAtRequest;
import dev.rayan.dto.request.client.ClientsByAddressFilterRequest;
import dev.rayan.dto.request.client.CreateClientRequest;
import dev.rayan.dto.request.client.UpdateClientRequest;
import dev.rayan.dto.response.client.FoundClientResponse;
import dev.rayan.dto.response.client.CreatedClientResponse;
import dev.rayan.dto.response.page.PageResponse;
import dev.rayan.exceptions.UserAlreadyExistsException;
import dev.rayan.mappers.ClientMapper;
import dev.rayan.model.Address;
import dev.rayan.model.Client;
import dev.rayan.model.Credential;
import dev.rayan.repositories.ClientRepository;
import dev.rayan.utils.PaginationUtils;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.NotAuthorizedException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static jakarta.ws.rs.core.Response.Status.GONE;
import static jakarta.ws.rs.core.Response.Status.UNAUTHORIZED;

@ApplicationScoped
public final class ClientService {

    @Inject
    ClientRepository repository;

    @Inject
    ClientMapper mapper;

    public CreatedClientResponse persist(final Credential credential, final CreateClientRequest request) {

        //If tryning create client again
        if (findByIdOptional(credential.getId()).isPresent()) {
            throw new UserAlreadyExistsException("Your register is already completed!", GONE);
        }

        if (repository.findCpf(request.cpf()).isPresent()) {
            throw new NotAuthorizedException("CPF already exists!", UNAUTHORIZED);
        }

        final Client client = mapper.requestToClient(credential, request);
        repository.persist(client);

        return mapper.clientToResponse(client);
    }

    public CreatedClientResponse findClientById(final UUID id) {
        return findByIdOptional(id)
                .map(mapper::clientToResponse)
                .orElseThrow(() -> new NotAuthorizedException("Client not exists!", UNAUTHORIZED));
    }

    public void update(final UUID id, final UpdateClientRequest request) {

        final Client client = findByIdOptional(id)
                .orElseThrow(() -> new NotAuthorizedException("You need complete your register!", UNAUTHORIZED));

        repository.updatePartial(client, request);
    }

    public void update(final UUID id, final Address address) {

        final Client client = findByIdOptional(id)
                .orElseThrow(() -> new NotAuthorizedException("You need complete your register!", UNAUTHORIZED));

        repository.updateAddress(client, address);
    }

    private Optional<Client> findByIdOptional(final UUID id) {
        return repository.findByIdOptional(id);
    }

    public PageResponse<FoundClientResponse> findClientsByCreatedAt(final ClientsByCreatedAtRequest request) {
        return PaginationUtils.paginate(
                repository.findClientsByCreatedAt(request),
                request.getPagination()
        );
    }

    public List<FoundClientResponse> findClientsByAddressFilter(final ClientsByAddressFilterRequest request) {

        final List<FoundClientResponse> clients = repository.findClientsByAddressFilter(request);
        return clients;

    }
}