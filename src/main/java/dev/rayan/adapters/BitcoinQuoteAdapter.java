package dev.rayan.adapters;


import dev.rayan.client.QuoteRestClient;
import dev.rayan.dto.respose.BitcoinQuotedResponseDTO;
import dev.rayan.mappers.BitcoinMapper;
import dev.rayan.model.bitcoin.Bitcoin;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import javax.swing.text.html.Option;
import java.awt.desktop.OpenFilesEvent;
import java.util.Optional;

@ApplicationScoped
public final class BitcoinQuoteAdapter {

    @Inject
    @RestClient
    QuoteRestClient quoteRestClient;

    public Optional<Bitcoin> quote() {return quoteRestClient.quote();}

}
