package dev.rayan.client;

import dev.rayan.model.bitcoin.Bitcoin;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.ws.rs.GET;
import org.eclipse.microprofile.faulttolerance.Fallback;
import org.eclipse.microprofile.faulttolerance.Retry;
import org.eclipse.microprofile.faulttolerance.Timeout;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import javax.swing.text.html.Option;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@RegisterRestClient(baseUri = "https://brasilbitcoin.com.br/API/prices/BTC")
public interface QuoteRestClient {

    @GET
    @Timeout(value = 2)
    @Retry(delayUnit = ChronoUnit.SECONDS, delay = 1, maxRetries = 2)
    @Fallback(fallbackMethod = "fallback")
    Optional<Bitcoin> quote();

    default Optional<Bitcoin> fallback() {
        return Optional.empty();
    }

}
