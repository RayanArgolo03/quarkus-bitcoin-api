package dev.rayan.adapter;


import dev.rayan.client.QuoteRestClient;
import dev.rayan.model.bitcoin.Bitcoin;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.util.Optional;

@ApplicationScoped
public final class BitcoinQuoteAdapter {

    @Inject
    @RestClient
    QuoteRestClient quoteRestClient;

    public Optional<Bitcoin> quote() {
        return quoteRestClient.quote();
    }

}
