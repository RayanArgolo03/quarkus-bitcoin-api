package dev.rayan.client;

import dev.rayan.dto.response.transaction.BitcoinResponse;
import jakarta.ws.rs.GET;
import org.eclipse.microprofile.faulttolerance.Fallback;
import org.eclipse.microprofile.faulttolerance.Retry;
import org.eclipse.microprofile.faulttolerance.Timeout;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import java.time.temporal.ChronoUnit;
import java.util.Optional;

@RegisterRestClient(configKey = "quote")
public interface QuoteRestClient {

    @GET
    @Timeout(value = 2, unit = ChronoUnit.SECONDS)
    @Retry(delayUnit = ChronoUnit.SECONDS, delay = 1, maxRetries = 2)
    @Fallback(fallbackMethod = "fallback")
    Optional<BitcoinResponse> quote();

    default Optional<BitcoinResponse> fallback() { return Optional.empty(); }

}
