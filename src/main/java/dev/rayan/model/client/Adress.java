package dev.rayan.model.client;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@NoArgsConstructor(force = true)
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)

@Embeddable
public class Adress {

    @Column(nullable = false, length = 8)
    String cep;

    @Column(nullable = false)
    String state;

    @Column(nullable = false)
    String street;

}
