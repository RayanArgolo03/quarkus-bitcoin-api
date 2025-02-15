package dev.rayan.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;


@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@EqualsAndHashCode

@Embeddable
public final class Address {

    @NotBlank(message = "Required CEP!")
    @Pattern(regexp = "^\\d{8}$", message = "The CEP should have only 8 numbers!")
    @Column(nullable = false, length = 8)
    @JsonProperty("cep")
    String cep;

    @NotEmpty(message = "Required state!")
    @Column(nullable = false)
    @JsonProperty("estado")
    @JsonAlias("state")
    String state;

    @NotEmpty(message = "Required street!")
    @Column(nullable = false)
    @JsonProperty("logradouro")
    @JsonAlias("street")
    String street;

    @NotEmpty(message = "Required neighbourhood!")
    @Column(nullable = false)
    @JsonProperty("bairro")
    @JsonAlias("neighbourhood")
    String neighbourhood;

    //Front-end should block any non-numeric character
    @NotEmpty(message = "Required house number!")
    @Pattern(regexp = "^[1-9]\\d*$", message = "House number should be greater than 1 and contain only numbers!")
    @Column(name = "house_number", nullable = false)
    @JsonProperty("houseNumber")
    String houseNumber;

}
