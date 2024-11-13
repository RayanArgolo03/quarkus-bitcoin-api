package dev.rayan.client;

import dev.rayan.model.bitcoin.Bitcoin;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.faulttolerance.Fallback;
import org.eclipse.microprofile.faulttolerance.Timeout;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@RegisterRestClient(baseUri = "https://brasilbitcoin.com.br/API/prices/BTC")
public interface QuoteRestClient {


    //Todo
    @GET
    @Produces(value = MediaType.APPLICATION_JSON)
    @Timeout(value = 1)
    @Fallback(fallbackMethod = "a")
    Bitcoin quote();

}
