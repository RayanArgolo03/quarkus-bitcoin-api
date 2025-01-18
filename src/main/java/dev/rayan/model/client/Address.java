package dev.rayan.model.client;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@NoArgsConstructor(force = true)
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)

@Embeddable
public final class Address {

    @Column(nullable = false, length = 8)
    String cep;

    @Column(nullable = false)
    String state;

    @Column(nullable = false)
    String street;

    @Column(nullable = false)
    String neighbourhood;

    @Column(name = "house_number", nullable = false)
    Integer houseNumber;

}
