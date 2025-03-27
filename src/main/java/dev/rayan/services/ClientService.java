package dev.rayan.services;

import dev.rayan.dto.request.client.ClientsByAddressFilterRequest;
import dev.rayan.dto.request.client.ClientsByCreatedAtRequest;
import dev.rayan.dto.request.client.CreateClientRequest;
import dev.rayan.dto.request.client.UpdateClientRequest;
import dev.rayan.dto.response.client.ClientResponse;
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
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.WebApplicationException;

import java.util.UUID;

import static jakarta.ws.rs.core.Response.Status.*;

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
            throw new WebApplicationException("Invalid CPF, use your document!", CONFLICT);
        }

        final Client client = mapper.requestToClient(credential, request);
        repository.persist(client);

        return mapper.clientToResponse(client);
    }

    public void update(final UUID id, final UpdateClientRequest request) {

        final Client client = repository.findByIdOptional(id)
                .orElseThrow(() -> new NotAuthorizedException("Client not exists, register incomplete!", UNAUTHORIZED));

        client.setFirstName(request.firstName());
        client.setSurname(request.surname());

        repository.persist(client);
    }

    public void update(final UUID id, final Address address) {

        final Client client = repository.findByIdOptional(id)
                .orElseThrow(() -> new NotAuthorizedException("Client not exists, register incomplete!", UNAUTHORIZED));

        if (address.equals(client.getAddress())) {
            throw new WebApplicationException("The new address is the same as the current address!", CONFLICT);
        }

        client.setAddress(address);

        repository.persist(client);
    }


    public ClientResponse findClientById(final UUID id) {
        return repository.findByIdOptional(id)
                .map(mapper::clientToResponse)
                .orElseThrow(() -> new NotFoundException("Client not found!"));
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

    public Client findClientByEmail(final String email) {
        return repository.find("credential.email = ?1", email)
                .singleResultOptional()
                .orElseThrow(() -> new NotFoundException("Client not found!"));
    }
}