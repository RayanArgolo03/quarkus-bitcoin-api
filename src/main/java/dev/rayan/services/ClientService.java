package dev.rayan.services;

import dev.rayan.dto.request.client.ClientsByAddressFilterRequest;
import dev.rayan.dto.request.client.ClientsByCreatedAtRequest;
import dev.rayan.dto.request.client.CreateClientRequest;
import dev.rayan.dto.request.client.UpdateClientRequest;
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

import java.util.Optional;
import java.util.UUID;

import static jakarta.ws.rs.core.Response.Status.GONE;
import static jakarta.ws.rs.core.Response.Status.UNAUTHORIZED;

@ApplicationScoped
public final class ClientService {

    private static final String REGISTER_INCOMPLETE_MESSAGE = "Client not exists, register incomplete!";
    
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
                .orElseThrow(() -> new NotAuthorizedException(REGISTER_INCOMPLETE_MESSAGE, UNAUTHORIZED));
    }

    public Client findClientByEmail(final String email){
        return repository.find("credential.email = ?1", email)
                .singleResultOptional()
                .orElseThrow(() -> new NotAuthorizedException(REGISTER_INCOMPLETE_MESSAGE, UNAUTHORIZED));
    }

    public void update(final UUID id, final UpdateClientRequest request) {

        final Client client = findByIdOptional(id)
                .orElseThrow(() -> new NotAuthorizedException(REGISTER_INCOMPLETE_MESSAGE, UNAUTHORIZED));

        repository.updatePartial(client, request);
    }

    public void update(final UUID id, final Address address) {

        final Client client = findByIdOptional(id)
                .orElseThrow(() -> new NotAuthorizedException(REGISTER_INCOMPLETE_MESSAGE, UNAUTHORIZED));

        repository.updateAddress(client, address);
    }

    private Optional<Client> findByIdOptional(final UUID id) {
        return repository.findByIdOptional(id);
    }

    public PageResponse findClientsByCreatedAt(final ClientsByCreatedAtRequest request) {
        return PaginationUtils.paginate(
                repository.findClientsByCreatedAt(request),
                request.getPagination()
        );
    }

    public PageResponse findClientsByAddressFilter(final ClientsByAddressFilterRequest request) {
        return PaginationUtils.paginate(
                repository.findClientsByAddressFilter(request),
                request.getPagination()
        );
    }
}