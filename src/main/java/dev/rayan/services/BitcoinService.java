package dev.rayan.services;

import dev.rayan.adapter.BitcoinQuoteAdapter;
import dev.rayan.dto.response.bitcoin.BitcoinResponse;
import dev.rayan.exceptions.ApiException;
import dev.rayan.mappers.BitcoinMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public final class BitcoinService {

    @Inject
    BitcoinMapper mapper;

    @Inject
    BitcoinQuoteAdapter adapter;

    public BitcoinResponse getMappedBitcoin() {
        return adapter.quote()
                .map(mapper::bitcoinToBitcoinQuoteResponse)
                .orElseThrow(() -> new ApiException("The server was unable to complete your request, contact @rayan_argolo"));
    }

}
