
package dev.rayan.scheduler;


import dev.rayan.repositories.ForgotPasswordRepository;
import dev.rayan.resources.TransactionResource;
import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import java.util.Map;

@ApplicationScoped
public final class ExpiredForgotPasswordScheduler {

    @Inject
    ForgotPasswordRepository repository;

    private static final Logger LOG = Logger.getLogger(ExpiredForgotPasswordScheduler.class);

    @ConfigProperty(name = "expired-time")
    String expiredTime;

    @ConfigProperty(name = "time-unit")
    String timeUnit;

    private final static String DELETE_BASE_QUERY = """
            {
                "$expr": {
                  "$lt": [
                    { "$dateAdd": { startDate: "$madeAt", unit: :timeUnit, amount: :amount } },
                    "$$NOW"
                  ]
                }
              }
            """;

    @Scheduled(every = "30m")
    public void deleteExpiredForgotPasswordRequestTime() {

        LOG.info("Scheduler deleting expired forgot password requests");

        final Map<String, Object> parameters = Map.of(
                "timeUnit", timeUnit,
                "amount", Long.parseLong(expiredTime)
        );

        repository.delete(DELETE_BASE_QUERY, parameters);
    }

}
