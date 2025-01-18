package dev.rayan.services;

import dev.rayan.client.ViaCepRestClient;
import dev.rayan.dto.respose.AdressResponse;
import dev.rayan.exceptions.ApiException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.rest.client.inject.RestClient;

@ApplicationScoped
public final class AdressService {

    @Inject
    @RestClient
    ViaCepRestClient viaCep;

    public AdressResponse findAdressByCep(final String cep) {
        return viaCep.findAdressByCep(cep)
                .orElseThrow(() -> new ApiException("The server was unable to complete your request, contact @rayan_argolo"));
    }
}
