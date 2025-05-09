package dev.rayan.services;

import dev.rayan.client.ViaCepRestClient;
import dev.rayan.exceptions.ApiException;
import dev.rayan.model.Address;
import io.quarkus.test.InjectMock;
import io.quarkus.test.component.QuarkusComponentTest;
import jakarta.inject.Inject;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static jakarta.ws.rs.core.Response.Status.NOT_FOUND;
import static jakarta.ws.rs.core.Response.Status.SERVICE_UNAVAILABLE;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@QuarkusComponentTest
@DisplayName("---- AddressService tests ---- ")
class AddressServiceTest {

    @Inject
    AddressService service;

    @InjectMock
    @RestClient
    ViaCepRestClient viaCepRestClient;

    String cep;

    @BeforeEach
    void setUp() {
        cep = "any";
    }

    @Nested
    @DisplayName("---- FindAddressByCep tests ----")
    class FindAddressByCepTests {

        @Test
        @DisplayName("Should be throw NotFoundException when cep not exists")
        void givenFindAddressByCep_whenCepNotExists_thenThrowNotFoundException() {

            when(viaCepRestClient.findAddressByCep(cep))
                    .thenReturn(Optional.of(new Address()));

            final NotFoundException e = assertThrows(NotFoundException.class,
                    () -> service.findAddressByCep(cep));


            final String expectedMessage = "CEP not exists!";
            final Response.Status expectedStatusCode = NOT_FOUND;

            assertEquals(expectedMessage, e.getMessage());
            assertEquals(expectedStatusCode, e.getResponse().getStatusInfo().toEnum());

            verify(viaCepRestClient).findAddressByCep(cep);
        }

        @Test
        @DisplayName("Should be call the fallback method and throw ApiException when ViaCep is down")
        void givenFindAddressByCep_whenViaCepApiIsDown_thenCallFallbackMethodAndThrowApiException() {

            when(viaCepRestClient.findAddressByCep(cep))
                    .thenReturn(Optional.empty());

            final ApiException e = assertThrows(ApiException.class,
                    () -> service.findAddressByCep(cep));

            final String expectedMessage = "The service is unavailable, contact the support in Linkedin @rayan_argolo";
            final Response.Status expectedStatusCode = SERVICE_UNAVAILABLE;

            assertEquals(expectedMessage, e.getMessage());
            assertEquals(expectedStatusCode, e.getResponse().getStatusInfo().toEnum());

            verify(viaCepRestClient).findAddressByCep(cep);
        }

        @Test
        @DisplayName("Should be return Address when the cep exists")
        void givenFindByAddressByCep_whenCepExists_thenReturnAddress() {

            Address address = new Address();
            address.setCep(cep);

            when(viaCepRestClient.findAddressByCep(cep))
                    .thenReturn(Optional.of(address));

            assertNotNull(service.findAddressByCep(cep));

            verify(viaCepRestClient).findAddressByCep(cep);

        }
    }

}