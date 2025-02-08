package dev.rayan.services;

import dev.rayan.client.ViaCepRestClient;
import dev.rayan.exceptions.ApiException;
import dev.rayan.exceptions.BusinessException;
import dev.rayan.model.Address;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.rest.client.inject.RestClient;

@ApplicationScoped
public final class AdressService {

    @Inject
    @RestClient
    ViaCepRestClient viaCep;

    public Address findAdressByCep(final String cep) {

        final Address address = viaCep.findAdressByCep(cep)
                .orElseThrow(() -> new ApiException("The server was unable to complete your request, contact @rayan_argolo"));

        if (address.getCep() == null) throw new BusinessException("CEP not exists!");

        return address;
    }
}
