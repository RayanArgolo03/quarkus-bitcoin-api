package dev.rayan.repositories;

import dev.rayan.model.Transaction;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.UUID;

@ApplicationScoped
public final class TransactionRepository implements PanacheRepositoryBase<Transaction, UUID> {


}
