package dev.rayan.model.client;


import dev.rayan.model.bitcoin.Transaction;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.Set;
import java.util.UUID;

@NoArgsConstructor(force = true)
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)

public final class Client {

    UUID id;
    String firstName;
    String surname;
    String cpf;
    Adress adress; //Todo embeaddeable

    String username;
    String password;
    Set<Transaction> transactions;


}
