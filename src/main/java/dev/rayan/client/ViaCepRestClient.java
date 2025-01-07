package dev.rayan.client;

import dev.rayan.dto.respose.AdressResponse;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import org.eclipse.microprofile.faulttolerance.Fallback;
import org.eclipse.microprofile.faulttolerance.Retry;
import org.eclipse.microprofile.faulttolerance.Timeout;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import java.time.temporal.ChronoUnit;
import java.util.Optional;

@RegisterRestClient(baseUri = "https://viacep.com.br/ws")
public interface ViaCepRestClient {

    @GET
    @Timeout(value = 1, unit = ChronoUnit.SECONDS)
    @Retry(delayUnit = ChronoUnit.SECONDS, delay = 1, maxRetries = 2)
    @Fallback(fallbackMethod = "fallback")
    @Path("/{cep}/json/")
    Optional<AdressResponse> findAdressByCep(@PathParam("cep") String cep);

    default Optional<AdressResponse> fallback(String cep) {
        return Optional.empty();
    }


}
