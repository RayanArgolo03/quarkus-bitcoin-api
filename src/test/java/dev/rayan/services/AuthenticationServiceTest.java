package dev.rayan.services;

import dev.rayan.dto.request.authentication.CredentialRequest;
import dev.rayan.mappers.CredentialMapper;
import dev.rayan.mappers.ForgotPasswordMapper;
import dev.rayan.repositories.CredentialRepository;
import dev.rayan.repositories.ForgotPasswordRepository;
import io.quarkus.test.InjectMock;
import io.quarkus.test.component.QuarkusComponentTest;
import io.quarkus.test.component.SkipInject;
import jakarta.inject.Inject;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

@QuarkusComponentTest
@ExtendWith(MockitoExtension.class)
@DisplayName("--- Authentication service tests ---")
public class AuthenticationServiceTest {

    @Inject
    AuthenticationService service;

    @InjectMock
    CredentialRepository credentialRepository;

    @InjectMock
    ForgotPasswordRepository forgotPasswordRepository;

    @Spy
    CredentialMapper credentialMapper;

    @Spy
    ForgotPasswordMapper forgotPasswordMapper;

    Validator hibernateValidator = Validation.buildDefaultValidatorFactory().getValidator();

    @Nested
    @DisplayName("--- Persist credential tests ")
    class PersistCredentialTests {

        @Test
        @DisplayName("Should be throw ConstraintViolationException when email is null")
        void givenPersistCredential_whenEmailIsNull_thenThrowConstraintViolationException() {

            final String password = "#Rayan12";
            final CredentialRequest request = new CredentialRequest(null, password);

            final Set<ConstraintViolation<CredentialRequest>> violations = hibernateValidator.validate(request);
            final String message = violations.iterator().next().getMessage();

            final String expectedMessage = "Email required!";
            final int expectedViolations = 1;

            assertEquals(expectedViolations, violations.size());
            assertEquals(expectedMessage, message);

        }

        @ParameterizedTest
        @ValueSource(strings = {
                "", " ", "plainaddress", "@missingusername.com", "username@.com", "username@.com.", "username@domain..com",
                "username@domain.c", "username@domain.toolong", "username@domain,com", "username@domain@domain.com",
                "username@domain..com", "username@domain.com (Joe Smith)", "username@domain.com.", "username@domain.com-",
                "username@domain.com_", "username@domain.com+", "username@domain.com%", "username@domain.com#",
                "username@domain.com$", "username@domain.com!", "username@domain.com^", "username@domain.com&",
                "username@domain.com*", "username@domain.com(", "username@domain.com)", "username@domain.com=",
                "username@domain.com{", "username@domain.com}", "username@domain.com[", "username@domain.com]"
        })
        @DisplayName("Should be throw ConstraintViolationException when email is invalid in pattern xxx@domain.com")
        void givenPersistCredential_whenEmailIsInvalid_thenThrowConstraintViolationException(@SkipInject final String email) {

            final String password = "#Rayan12";
            final CredentialRequest request = new CredentialRequest(email, password);

            final Set<ConstraintViolation<CredentialRequest>> violations = hibernateValidator.validate(request);
            final String message = violations.iterator().next().getMessage();

            final String expectedMessage = "Invalid email! Pattern required: xxx@domain.com";
            final int expectedViolations = 1;

            assertEquals(expectedViolations, violations.size());
            assertEquals(expectedMessage, message);

        }
    }

}

