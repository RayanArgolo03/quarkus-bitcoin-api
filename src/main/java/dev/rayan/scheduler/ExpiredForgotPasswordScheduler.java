
package dev.rayan.scheduler;


import dev.rayan.repositories.ForgotPasswordRepository;
import io.quarkus.panache.common.Parameters;
import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
public final class ExpiredForgotPasswordScheduler {

    @Inject
    ForgotPasswordRepository repository;

    @ConfigProperty(name = "expired-time")
    String expiredTime;

    @ConfigProperty(name = "time-unit")
    String timeUnit;

    private final static String DELETE_QUERY = """
            {
                "$expr": {
                  "$lt": [
                    { "$dateAdd": { "startDate": "madeAt", "unit": ":timeUnit", "amount": ":convertedExpiredTime" } },
                    "$$NOW"
                  ]
                }
              }
            """;

    @Scheduled(every = "{scheduler-time")
    private void deleteExpiredForgotPasswordRequest() {
        final long convertedExpiredTime = Long.parseLong(expiredTime);
        long convertedExpiredTime1 = repository.delete(DELETE_QUERY, Parameters.with("convertedExpiredTime", convertedExpiredTime));
        System.out.println();
    }

}
