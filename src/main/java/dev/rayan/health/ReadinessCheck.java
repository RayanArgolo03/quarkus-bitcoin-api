package dev.rayan.health;


import dev.rayan.client.QuoteRestClient;
import dev.rayan.client.ViaCepRestClient;
import dev.rayan.services.KeycloakService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.HealthCheckResponseBuilder;
import org.eclipse.microprofile.health.Readiness;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;

@Readiness
@ApplicationScoped
public class ReadinessCheck implements HealthCheck {

    @ConfigProperty(name = "keycloak.admin")
    String admin;

    @Inject
    KeycloakService keycloakService;

    @Inject
    @RestClient
    ViaCepRestClient viaCepRestClient;

    @Inject
    @RestClient
    QuoteRestClient quoteRestClient;

    private static final Logger LOG = Logger.getLogger(ReadinessCheck.class);

    private final HealthCheckResponseBuilder builder = HealthCheckResponse.named("External services health check")
            .up();

    @Override
    public HealthCheckResponse call() {

        LOG.info("Start readiness health check");

        if (viaCepRestClient.findAddressByCep("71918360").isEmpty()) {
            appendDownData("Via cep", "viacep api is down");
        }

        if (quoteRestClient.quote().isEmpty()) {
            appendDownData("Bitcoin quote", "bitcoin quote is down");
        }

        return builder.build();
    }

    private void appendDownData(final String service, final String message) {
        LOG.infof("%s is down", service);
        builder.withData(service, message)
                .down();
    }

    /*
     * keycloak, grafana, prometheus
     * */
}
