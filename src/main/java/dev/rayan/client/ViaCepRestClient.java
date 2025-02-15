package dev.rayan.client;

import dev.rayan.model.Address;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.faulttolerance.Fallback;
import org.eclipse.microprofile.faulttolerance.Retry;
import org.eclipse.microprofile.faulttolerance.Timeout;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import java.time.temporal.ChronoUnit;
import java.util.Optional;

@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@RegisterRestClient(configKey = "viacep")
public interface ViaCepRestClient {

    @GET
    @Timeout(value = 1, unit = ChronoUnit.SECONDS)
    @Retry(delayUnit = ChronoUnit.SECONDS, delay = 1, maxRetries = 2)
    @Fallback(fallbackMethod = "fallback")
    @Path("/{cep}/json/")
    Optional<Address> findAdressByCep(@PathParam("cep") String cep);

    default Optional<Address> fallback(final String cep) {
        return Optional.empty();
    }


}
