package dev.rayan.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor

@Embeddable
public final class Address {

    @Setter
    @NotBlank(message = "Required CEP!")
    @Pattern(regexp = "^\\d{8}$", message = "The CEP should have strictly 8 numbers!")
    @JsonProperty("cep")
    @Column(nullable = false, length = 8)
    String cep;

    //Block by front-end
    @NotBlank(message = "Required state!")
    @JsonProperty("estado")
    @JsonAlias("state")
    @Column(nullable = false)
    String state;

    //Block by front-end
    @NotBlank(message = "Required street!")
    @JsonProperty("logradouro")
    @JsonAlias("street")
    @Column(nullable = false)
    String street;

    //Block by front-end
    @NotBlank(message = "Required neighbourhood!")
    @JsonProperty("bairro")
    @JsonAlias("neighbourhood")
    @Column(nullable = false)
    String neighbourhood;

    @Setter
    @NotBlank(message = "Required house number!")
    @Pattern(regexp = "^[1-9]\\d*$", message = "House number should be greater than 1 and contain only numbers!")
    @JsonProperty("houseNumber")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Column(name = "house_number", nullable = false)
    String houseNumber;

}
