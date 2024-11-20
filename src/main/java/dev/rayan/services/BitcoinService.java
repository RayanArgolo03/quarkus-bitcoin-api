package dev.rayan.services;

import dev.rayan.adapters.BitcoinQuoteAdapter;
import dev.rayan.dto.respose.BitcoinQuotedResponseDTO;
import dev.rayan.mappers.BitcoinMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;

import static jakarta.ws.rs.core.Response.Status.*;

@ApplicationScoped
public final class BitcoinService {

    @Inject
    BitcoinMapper mapper;

    @Inject
    BitcoinQuoteAdapter adapter;

    public BitcoinQuotedResponseDTO getMappedBitcoin() {
        return adapter.quote()
                .map(mapper::bitcoinToBitcoinQuoteResponse)
                .orElseThrow(() -> new WebApplicationException("", SERVICE_UNAVAILABLE));
    }

}
