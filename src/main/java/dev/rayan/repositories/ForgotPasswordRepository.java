package dev.rayan.repositories;

import dev.rayan.model.ForgotPassword;
import io.quarkus.mongodb.panache.PanacheMongoRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public final class ForgotPasswordRepository implements PanacheMongoRepositoryBase<ForgotPassword, String> {
    public void deleteForgotPassword(final String code) {  deleteById(code); }

}
