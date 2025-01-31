package dev.rayan.model.client;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.FieldDefaults;


@FieldDefaults(level = AccessLevel.PRIVATE)
@ToString

@Embeddable
public final class Address {

    @NotBlank(message = "Required CEP!")
    @Pattern(regexp = "^\\d{8}$", message = "The CEP should have only 8 numbers!")
    @Column(nullable = false, length = 8)
    @JsonProperty("cep")
    String cep;

    @NotEmpty(message = "Required state!")
    @Column(nullable = false)
    @JsonProperty("state")
    String state;

    @NotEmpty(message = "Required street!")
    @Column(nullable = false)
    @JsonProperty("street")
    String street;

    @NotEmpty(message = "Required neighbourhood!")
    @Column(nullable = false)
    @JsonProperty("neighbourhood")
    String neighbourhood;

    //Front-end should block any non-numeric character
    @NotEmpty(message = "Required house number!")
    @Pattern(regexp = "^[1-9]\\d*$", message = "House number should be greater than 1 and contain only numbers!")
    @Column(name = "house_number", nullable = false)
    @JsonProperty("houseNumber")
    String houseNumber;

    //Setters used by Jackson for desserialize Viacep API request
    @JsonSetter("cep")
    private void setCep(String cep) {
        this.cep = cep;
    }

    @JsonSetter("estado")
    private void setState(String state) {
        this.state = state;
    }

    @JsonSetter("logradouro")
    private void setStreet(String street) {
        this.street = street;
    }

    @JsonSetter("bairro")
    private void setNeighbourhood(String neighbourhood) {
        this.neighbourhood = neighbourhood;
    }

    @JsonSetter("houseNumber")
    private void setHouseNumber(String houseNumber) {
        this.houseNumber = houseNumber;
    }
}
