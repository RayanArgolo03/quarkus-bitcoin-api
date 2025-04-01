package dev.rayan.services;

import dev.rayan.client.QuoteRestClient;
import dev.rayan.dto.response.transaction.BitcoinResponse;
import dev.rayan.exceptions.ApiException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.metrics.MetricUnits;
import org.eclipse.microprofile.metrics.annotation.Gauge;
import org.eclipse.microprofile.rest.client.inject.RestClient;

@ApplicationScoped
public final class BitcoinService {

    @Inject
    @RestClient
    QuoteRestClient quoteRestClient;

    public BitcoinResponse quote() {
        return quoteRestClient.quote()
                .orElseThrow(ApiException::new);
    }

    @Gauge(
            name = "bitcoins.current.price",
            absolute = true,
            description = "Current bitcoin price",
            unit = MetricUnits.NONE
    )
    public double getCurrentPrice() {
        return quote().price()
                .doubleValue();
    }

}
