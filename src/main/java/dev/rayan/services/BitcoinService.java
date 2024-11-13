package dev.rayan.services;


import dev.rayan.client.QuoteRestClient;
import dev.rayan.dto.respose.BitcoinQuoteResponse;
import dev.rayan.mappers.BitcoinMapper;
import dev.rayan.model.bitcoin.Bitcoin;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.rest.client.inject.RestClient;

@ApplicationScoped
public final class BitcoinService {

    @Inject
    @RestClient
    QuoteRestClient quoteRestClient;

    @Inject
    BitcoinMapper mapper;

    public BitcoinQuoteResponse quoteBitcoin() {

        //Todo adicione try-catch na requisição externa, tolerancia a falha
        final Bitcoin bitcoin = quoteRestClient.quote();

        return mapper.bitcoinToBitcoinQuoteResponse(bitcoin);
    }

}
