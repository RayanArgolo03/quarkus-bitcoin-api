package dev.rayan.jwt;

import io.smallrye.jwt.build.Jwt;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.eclipse.microprofile.jwt.Claims;

import java.util.Arrays;
import java.util.HashSet;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class JWTGeneration {

    private static final String ISSUER = "https://github.com/RayanArgolo03";

    public static void main(String[] args) {
        String token = Jwt.issuer(ISSUER)
                .upn("ishaq@quarkus.io")
                .groups(new HashSet<>(Arrays.asList("User", "Admin")))
                .claim(Claims.birthdate.name(), "2024-04-31")
                .sign();
        System.out.println(token);

    }

}
