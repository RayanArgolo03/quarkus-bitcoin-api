package dev.rayan.health;

import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Liveness;

@ApplicationScoped //Se não for definido será Singleton, precisa de instância global

@Liveness //Se não marcar com anotação nenhum check será contabilizado
//HealthCheck é interface funcional
public final class LivenessCheck implements HealthCheck {

    private static final String MESSAGE = "Application instance is available";

    @Override
    //HealthCheckResponse segue padrão Fluent Builder
    //Tem HealthCheckBuilder abstrato o qual é implementado em execução por ResponseBuilder
    public HealthCheckResponse call() {
        return HealthCheckResponse.up(MESSAGE);
    }
}
