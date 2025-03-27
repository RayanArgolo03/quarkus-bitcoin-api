package dev.rayan.services;

import dev.rayan.client.QuoteRestClient;
import dev.rayan.dto.response.transaction.BitcoinResponse;
import dev.rayan.exceptions.ApiException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.rest.client.inject.RestClient;

@ApplicationScoped
public final class BitcoinService {

    @Inject
    @RestClient
    QuoteRestClient quoteRestClient;

    public BitcoinResponse quote() {
        return quoteRestClient.quote()
                .orElseThrow(() -> new ApiException("The server was unable to complete your request, contact @rayan_argolo"));
    }

}
