package dev.rayan.services;

import dev.rayan.client.ViaCepRestClient;
import dev.rayan.exceptions.ApiException;
import dev.rayan.model.Address;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.NotFoundException;
import org.eclipse.microprofile.rest.client.inject.RestClient;

@ApplicationScoped
public class AddressService {

    @Inject
    @RestClient
    ViaCepRestClient viaCepRestClient;

    //throw ApiException if the ViaCepApi downtime
    public Address findAddressByCep(final String cep) {
        return viaCepRestClient.findAddressByCep(cep)
                .map(addressFound -> {
                    if (addressFound.getCep() == null) throw new NotFoundException("CEP not exists!");
                    return addressFound;
                })
                .orElseThrow(ApiException::new);
    }

}
