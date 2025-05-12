package dev.rayan.resources;

import dev.rayan.model.Address;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import static io.restassured.RestAssured.filters;
import static io.restassured.RestAssured.given;
import static jakarta.ws.rs.core.Response.Status.OK;
import static org.junit.jupiter.api.Assertions.assertEquals;

@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Tag("integration")
@DisplayName("---- AddressResource tests ----")
public class AddressResourceTest extends AccessTokenProvider {

    private static final String RESOUCE_PATH = "/api/v1/addresses";

    @BeforeAll
    static void beforeAll() {
        filters(new RequestLoggingFilter(), new ResponseLoggingFilter());
    }

    @TestInstance(Lifecycle.PER_CLASS)
    @Nested
    class FindAddressByCepTests {

        private static final String CEP_PATH = RESOUCE_PATH + "/{cep}";

        @Test
        @Order(1)
        void givenFindAddressByCep_whenCepExists_thenReturnResponse() {

            final String accessToken = getAccessToken(),
                    foundCep = "71918-360",
                    entityCep = "71918360";

            final int expectedStatusCode = OK.getStatusCode();

            final Address expectedBody = new Address(
                    foundCep,
                    "Distrito Federal",
                    "Rua 31",
                    "Norte (Águas Claras)",
                    null
            );

            final Response response =
                    given().
                            contentType(ContentType.JSON).
                            auth().oauth2(accessToken).
                            pathParam("cep", entityCep).
                            when().
                            get(CEP_PATH);

            assertEquals(expectedStatusCode, response.statusCode());
            assertEquals(expectedBody, response.as(Address.class));
        }
    }
}

/*
 *  //não autenticado,
 *  cep vazio,
 *  cep inválido
 * usuário inexistente,
 * usuário deslogado,
 *
 * */


