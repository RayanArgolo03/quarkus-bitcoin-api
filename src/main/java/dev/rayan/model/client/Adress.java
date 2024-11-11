package dev.rayan.model.client;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@NoArgsConstructor(force = true)
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)

public final class Adress {

    String cep; //Todo pesquisa convenção de tipo de dado
    String state;
    String street;
    String houseNumber;

}
