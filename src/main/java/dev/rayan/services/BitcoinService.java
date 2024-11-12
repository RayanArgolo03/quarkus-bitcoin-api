package dev.rayan.services;


import dev.rayan.client.QuoteRestClient;
import dev.rayan.dto.respose.BitcoinQuoteResponse;
import dev.rayan.model.bitcoin.Bitcoin;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Path;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;

@ApplicationScoped
public final class BitcoinService {

    @Inject
    @RestClient
    QuoteRestClient quoteRestClient;

    public BitcoinQuoteResponse quoteBitcoin() {
        Bitcoin bitcoin = quoteRestClient.quote();

        //Todo use Mapstruct
        return new BitcoinQuoteResponse(
                bitcoin.getLast().toString(),
                bitcoin.getTime().format(DateTimeFormatter.ofPattern("dd/MM/uuuu HH:mm"))
        );
    }

}
