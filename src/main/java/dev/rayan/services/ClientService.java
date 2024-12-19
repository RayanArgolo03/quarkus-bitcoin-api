package dev.rayan.services;


import dev.rayan.model.client.Client;
import dev.rayan.repositories.ClientRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;

@ApplicationScoped
public final class ClientService {

    @Inject
    ClientRepository repository;

    public List<Client> findClients() {
        return repository.listAll();
    }

}
