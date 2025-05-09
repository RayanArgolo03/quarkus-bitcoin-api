package dev.rayan.health;

import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Readiness;

@Readiness
@ApplicationScoped
public final class ReadinessCheck implements HealthCheck {

    //Todo
    @Override
    public HealthCheckResponse call() {
        return HealthCheckResponse.builder().build();
    }
}
