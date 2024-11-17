package dev.rayan.client;

import dev.rayan.model.bitcoin.Bitcoin;
import jakarta.ws.rs.GET;
import org.eclipse.microprofile.faulttolerance.Fallback;
import org.eclipse.microprofile.faulttolerance.Retry;
import org.eclipse.microprofile.faulttolerance.Timeout;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import java.time.temporal.ChronoUnit;

@RegisterRestClient(baseUri = QuoteRestClient.BASE_URI)
public interface QuoteRestClient {

    String BASE_URI = "https://brasilbitcoin.com.br/API/prices/BTC";

    @GET
    @Timeout(value = 2, unit = ChronoUnit.SECONDS)
    @Retry(delayUnit = ChronoUnit.SECONDS, delay = 1, maxRetries = 2)
    @Fallback(fallbackMethod = "fallback")
    Bitcoin quote();

    default Bitcoin fallback() {
        return null;
    }

}
