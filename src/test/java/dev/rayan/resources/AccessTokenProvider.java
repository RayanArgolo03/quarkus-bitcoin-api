package dev.rayan.resources;

import io.restassured.http.ContentType;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.keycloak.OAuth2Constants;

import java.util.Map;

import static io.restassured.RestAssured.given;
import static jakarta.ws.rs.core.Response.Status.OK;

public abstract class AccessTokenProvider {

    @ConfigProperty(name = "defaut-username")
    String username;

    @ConfigProperty(name = "defaut-password")
    String password;

    @ConfigProperty(name = "keycloak.access-token-url")
    String accessTokenUrl;

    @ConfigProperty(name = "keycloak.client-id")
    String clientId;

    @ConfigProperty(name = "keycloak.secret")
    String secret;

    protected String getAccessToken() {
        return
            given().
                contentType(ContentType.URLENC).
                formParams(Map.of(
                        "client_id", clientId,
                        "client_secret", secret,
                        "username", username,
                        "password", password,
                        "grant_type", OAuth2Constants.PASSWORD
                        )).
            when().
                post(accessTokenUrl).
            then().
                assertThat().statusCode(OK.getStatusCode()).
                extract().path(OAuth2Constants.ACCESS_TOKEN);
    }
}