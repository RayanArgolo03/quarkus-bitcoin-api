package dev.rayan.health;


import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Liveness;
import org.jboss.logging.Logger;

import static java.lang.String.format;

@Liveness
@ApplicationScoped
public class LivenessCheck implements HealthCheck {

    @ConfigProperty(name = "quarkus.http.port")
    String port;

    private static final Logger LOG = Logger.getLogger(ReadinessCheck.class);

    @Override
    public HealthCheckResponse call() {

        LOG.info("Start liveness check");

        return HealthCheckResponse.up(format(
                "Application is up in the port %s!",
                port
        ));
    }
}
