package dev.rayan.model.client;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.Min;
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
    @JsonProperty("estado")
    String state;

    @Column(nullable = false)
    @JsonProperty("logradouro")
    String street;

    @Column(nullable = false)
    @JsonProperty("bairro")
    String neighbourhood;

    @Column(name = "house_number", nullable = false)
    @Min(value = 1, message = "House number can´t be less than 1!")
    @JsonProperty("houseNumber")
    Integer houseNumber;


}
