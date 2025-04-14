package dev.rayan.services;

import dev.rayan.dto.request.authentication.*;
import dev.rayan.dto.response.client.CredentialResponse;
import dev.rayan.dto.response.client.ForgotPasswordResponse;
import dev.rayan.mappers.CredentialMapper;
import dev.rayan.mappers.ForgotPasswordMapper;
import dev.rayan.model.Credential;
import dev.rayan.model.ForgotPassword;
import dev.rayan.repositories.CredentialRepository;
import dev.rayan.repositories.ForgotPasswordRepository;
import dev.rayan.utils.CryptographyUtils;
import io.quarkus.test.InjectMock;
import io.quarkus.test.component.QuarkusComponentTest;
import io.quarkus.test.component.SkipInject;
import jakarta.inject.Inject;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.ws.rs.ForbiddenException;
import jakarta.ws.rs.NotAuthorizedException;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static jakarta.ws.rs.core.Response.Status.*;
import static java.lang.String.format;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@QuarkusComponentTest
@DisplayName("--- Authentication service tests ---")
public class AuthenticationServiceTest {

    @Inject
    AuthenticationService service;

    @InjectMock
    CredentialRepository credentialRepository;

    @InjectMock
    ForgotPasswordRepository forgotPasswordRepository;

    @InjectMock
    CredentialMapper credentialMapper = Mappers.getMapper(CredentialMapper.class);

    @InjectMock
    ForgotPasswordMapper forgotPasswordMapper = Mappers.getMapper(ForgotPasswordMapper.class);

    Validator hibernateValidator = Validation.buildDefaultValidatorFactory().getValidator();

    @Nested
    @DisplayName("--- CredentialRequest tests ")
    class CredentialRequestTests {

        @Test
        @DisplayName("Must have one violation when email is null")
        void givenPersistCredential_whenEmailIsNull_thenThrowConstraintViolationException() {

            final String password = "#Rayan12";
            final CredentialRequest request = new CredentialRequest(new EmailRequest(null), password);

            final Set<ConstraintViolation<CredentialRequest>> violations = hibernateValidator.validate(request);

            final String message = violations.iterator().next().getMessage(),
                    expectedMessage = "Email required!";

            final int size = violations.size(),
                    expectedSize = 1;

            assertEquals(expectedSize, size);
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
        @DisplayName("Must have one violation when email is invalid at pattern xxx@domain.com")
        void givenPersistCredential_whenEmailIsInvalid_thenThrowConstraintViolationException(@SkipInject final String email) {

            final String password = "#Rayan12";
            final CredentialRequest request = new CredentialRequest(new EmailRequest(email), password);

            final Set<ConstraintViolation<CredentialRequest>> violations = hibernateValidator.validate(request);

            final String message = violations.iterator().next().getMessage(),
                    expectedMessage = "Invalid email! Pattern required: xxx@domain.com";

            final int size = violations.size(),
                    expectedSize = 1;

            assertEquals(expectedSize, size);
            assertEquals(expectedMessage, message);

        }

        @Test
        @DisplayName("Must have one violation when password is null")
        void givenPersistCredential_whenPasswordIsNull_thenThrowConstraintViolationException() {

            final String email = "admin@gmail.com";
            final CredentialRequest request = new CredentialRequest(new EmailRequest(email), null);

            final Set<ConstraintViolation<CredentialRequest>> violations = hibernateValidator.validate(request);

            final String message = violations.iterator().next().getMessage(),
                    expectedMessage = "Password required!";

            final int size = violations.size(),
                    expectedSize = 1;

            assertEquals(expectedSize, size);
            assertEquals(expectedMessage, message);
        }

        @ParameterizedTest
        @ValueSource(strings = {
                "abcde", "ABCDE", "12345", "abc123", "ABC123", "abc!@#", "123!@#", "abcABC", "abcABC123",
                "abcABC!@#", "123ABC!@#", "abc123!@#", "abcABC123!@#", "abcdE", "abcdE1",
                "abcdE1!@#", "abcdE1!@#2", "abcdE1!@#23", "abcdE1!@#234", "abcdE1!@#2345", "abcdE1!@#23456", "abcdE1!@#234567",
                "abcdE1!@#2345678", "abcdE1!@#23456789", "abcdE1!@#234567890", "abcdE1!@#2345678901"
        })
        @DisplayName("Must have one violation when password is invalid at pattern '5 and 8 characters, at least 1 special character and a capital letter'")
        void givenPersistCredential_whenPasswordIsInvalid_thenThrowConstraintViolationException(@SkipInject final String password) {

            final String email = "admin@gmail.com";
            final CredentialRequest request = new CredentialRequest(new EmailRequest(email), password);

            final Set<ConstraintViolation<CredentialRequest>> violations = hibernateValidator.validate(request);

            final String message = violations.iterator().next().getMessage(),
                    expectedMessage = "Invalid password! Between 5 and 8 characters, at least 1 special character and a capital letter!";

            final int size = violations.size(),
                    expectedSize = 1;

            assertEquals(expectedSize, size);
            assertEquals(expectedMessage, message);

        }
    }


    @Nested
    @DisplayName("---- Persist credential tests ----")
    class PersistCredentialTests {

        CredentialRequest request;

        @BeforeEach
        void setUp() {
            request = new CredentialRequest(new EmailRequest("admin@gmail.com"), "#Admin12");
        }

        @Test
        @DisplayName("Should be throw WebApplicationException with status code 409 Conflict when email already exists")
        void givenPersistCredential_whenRequestEmailIsPresent_thenThrowWebApplicationException() {


            when(credentialRepository.findCredentialByEmail(request.emailRequest().email()))
                    .thenReturn(Optional.of(new Credential()));

            final WebApplicationException e = assertThrows(WebApplicationException.class,
                    () -> service.persistCredential(request));

            final String expectedMessage = format("Client with email %s already exists!", request.emailRequest().email());
            final Response.Status expectedStatusCode = CONFLICT;

            assertEquals(expectedMessage, e.getMessage());
            assertEquals(expectedStatusCode, e.getResponse().getStatusInfo().toEnum());

            verify(credentialRepository, times(1)).findCredentialByEmail(request.emailRequest().email());
        }

        @Test
        @DisplayName("Should persist and return CredentialResponse when request is valid")
        void givenPersistCredential_whenRequestIsValid_thenPersistAndReturnCredentialResponse() {

            final Credential credential = Credential.builder()
                    .id(UUID.randomUUID())
                    .email(request.emailRequest().email())
                    .password(CryptographyUtils.encrypt(request.password()))
                    .build();

            final CredentialResponse expectedResponse = CredentialResponse.builder()
                    .id(credential.getId())
                    .email(credential.getEmail())
                    .password(credential.getPassword())
                    .createdAt(credential.getCreatedAt())
                    .keycloakUserId(null)
                    .build();

            when(credentialRepository.findCredentialByEmail(request.emailRequest().email()))
                    .thenReturn(Optional.empty());

            when(credentialMapper.requestToCredential(request)).thenReturn(credential);
            doNothing().when(credentialRepository).persist(credential);
            when(credentialMapper.credentialToResponse(credential)).thenReturn(expectedResponse);

            final CredentialResponse response = service.persistCredential(request);

            assertEquals(response, expectedResponse);

            verify(credentialRepository).findCredentialByEmail(request.emailRequest().email());
            verify(credentialMapper).requestToCredential(request);
            verify(credentialRepository).persist(credential);
            verify(credentialMapper).credentialToResponse(credential);
        }
    }

    @Nested
    @DisplayName("---- Login tests ----")
    class LoginTests {

        CredentialRequest request;

        @BeforeEach
        void setUp() {
            request = new CredentialRequest(new EmailRequest("login@gmail.com"), "#Logi1");
        }

        @Test
        @DisplayName("Should be throw NotAuthorizedException with status code 401 Unauthorized when credential not exists")
        void givenLogin_whenCredentialNotExistsByEmail_thenThrowNotAuthorizedException() {

            when(credentialRepository.findCredentialByEmail(request.emailRequest().email()))
                    .thenReturn(Optional.empty());

            final NotAuthorizedException e = assertThrows(NotAuthorizedException.class,
                    () -> service.login(request));

            final String expectedMessage = "Account not exists!";
            final Response.Status expectedStatusCode = UNAUTHORIZED;

            assertEquals(expectedMessage, e.getMessage());
            assertEquals(expectedStatusCode, e.getResponse().getStatusInfo().toEnum());

            verify(credentialRepository).findCredentialByEmail(request.emailRequest().email());
        }

        @Test
        @DisplayName("Should be throw NotAuthorizedException with status code 401 Unauthorized when password is incorrect")
        void givenLogin_whenPasswordIsIncorrect_thenThrowNotAuthorizedException() {

            final String credentialPassword = CryptographyUtils.encrypt(
                    request.password() + "!"
            );

            final Credential credential = Credential.builder()
                    .email(request.emailRequest().email())
                    .password(credentialPassword)
                    .build();

            when(credentialRepository.findCredentialByEmail(request.emailRequest().email()))
                    .thenReturn(Optional.of(credential));

            final NotAuthorizedException e = assertThrows(NotAuthorizedException.class,
                    () -> service.login(request));

            final String expectedMessage = "Use a valid password!";
            final Response.Status expectedStatusCode = UNAUTHORIZED;

            assertEquals(expectedMessage, e.getMessage());
            assertEquals(expectedStatusCode, e.getResponse().getStatusInfo().toEnum());

        }

        @Test
        @DisplayName("Should be return CredentialResponse when request is valid")
        void givenLogin_whenRequestIsValid_thenReturnCredentialResponse() {

            final String credentialPassword = CryptographyUtils.encrypt(request.password());

            final Credential credential = Credential.builder()
                    .email(request.emailRequest().email())
                    .password(credentialPassword)
                    .build();

            final CredentialResponse expectedResponse = CredentialResponse.builder()
                    .id(UUID.randomUUID())
                    .email(credential.getEmail())
                    .password(credential.getPassword())
                    .createdAt(credential.getCreatedAt())
                    .keycloakUserId(null)
                    .build();

            when(credentialRepository.findCredentialByEmail(request.emailRequest().email()))
                    .thenReturn(Optional.of(credential));

            when(credentialMapper.credentialToResponse(credential)).thenReturn(expectedResponse);

            assertEquals(expectedResponse, service.login(request));

            verify(credentialRepository).findCredentialByEmail(request.emailRequest().email());

        }
    }

    @Nested
    @DisplayName("---- Persist forgot password tests")
    class PersistForgotPasswordTests {

        EmailRequest request;

        @BeforeEach
        void setUp() {
            request = new EmailRequest("locale@email.com");
        }

        @Test
        @DisplayName("Should be throw NotAuthorizedException with status code Unauthorized 401 when credential not exists by email")
        void givenPersistForgotPassword_whenCredentialNotExistsByEmail_thenThrowNotAuthorizedException() {

            when(credentialRepository.findCredentialByEmail(request.email()))
                    .thenReturn(Optional.empty());

            final NotAuthorizedException e = assertThrows(NotAuthorizedException.class,
                    () -> service.persistForgotPassword(request));

            final String expectedMessage = "Account not exists!";
            final Response.Status expectedStatusCode = UNAUTHORIZED;

            assertEquals(expectedMessage, e.getMessage());
            assertEquals(expectedStatusCode, e.getResponse().getStatusInfo().toEnum());

            verify(credentialRepository).findCredentialByEmail(request.email());
        }

        @Test
        @DisplayName("Should persist and return ForgotPasswordResponse when request is valid")
        void givenPersistForgotPassword_whenRequestIsValid_thenPersistAndReturnForgotPasswordResponse() {

            final Credential credential = Credential.builder()
                    .id(UUID.randomUUID())
                    .build();

            final ForgotPassword forgotPassword = new ForgotPassword(credential.getId());

            final ForgotPasswordResponse expectedResponse = new ForgotPasswordResponse(
                    forgotPassword.getCode(), forgotPassword.getMadeAt()
            );

            final ArgumentCaptor<ForgotPassword> forgotPasswordCaptor = ArgumentCaptor.forClass(ForgotPassword.class);

            when(credentialRepository.findCredentialByEmail(request.email()))
                    .thenReturn(Optional.of(credential));

            doNothing().when(forgotPasswordRepository)
                    .persist(forgotPasswordCaptor.capture());

            when(forgotPasswordMapper.forgotPasswordToResponse(any(ForgotPassword.class)))
                    .thenReturn(expectedResponse);

            assertEquals(expectedResponse, service.persistForgotPassword(request));

            final ForgotPassword forgotPasswordCaptured = forgotPasswordCaptor.getValue();

            verify(credentialRepository).findCredentialByEmail(request.email());
            verify(forgotPasswordRepository).persist(forgotPasswordCaptured);
            verify(forgotPasswordMapper).forgotPasswordToResponse(any(ForgotPassword.class));

            assertEquals(forgotPasswordCaptured.getCredentialId(), forgotPassword.getCredentialId());
        }
    }


    @Nested
    @DisplayName("---- ForgotPasswordRequestTests ----")
    class ForgotPasswordRequestTests {

        @Test
        @DisplayName("Must have one violation when email is null")
        void givenUpdateForgotPassword_whenEmailIsNull_thenThrowConstraintViolationException() {

            final String code = "8c878e6fff134a37a2087510c2638944";
            final ForgotPasswordRequest request = new ForgotPasswordRequest(null, code);

            final Set<ConstraintViolation<ForgotPasswordRequest>> violations = hibernateValidator.validate(request);

            final String message = violations.iterator().next().getMessage(),
                    expectedMessage = "Email required!";

            final int size = violations.size(),
                    expectedSize = 1;

            assertEquals(expectedSize, size);
            assertEquals(expectedMessage, message);
        }


        @ParameterizedTest
        @ValueSource(strings = {
                "plainaddress", "@missingusername.com", "username@.com", "username@.com.", "username@domain..com",
                "username@domain.c", "username@domain.toolong", "username@domain,com", "username@domain@domain.com",
                "username@domain..com", "username@domain.com (Joe Smith)", "username@domain.com.", "username@domain.com-",
                "username@domain.com_", "username@domain.com+", "username@domain.com%", "username@domain.com#",
                "username@domain.com$", "username@domain.com!", "username@domain.com^", "username@domain.com&",
                "username@domain.com*", "username@domain.com(", "username@domain.com)", "username@domain.com=",
                "username@domain.com{", "username@domain.com}", "username@domain.com[", "username@domain.com]"
        })
        @DisplayName("Must have one violation when email is invalid at pattern xxx@domain.com")
        void givenUpdateForgotPassword_whenEmailIsInvalid_thenThrowConstraintViolationException(@SkipInject final String email) {

            final String code = "8c878e6fff134a37a2087510c2638944";
            final ForgotPasswordRequest request = new ForgotPasswordRequest(email, code);

            final Set<ConstraintViolation<ForgotPasswordRequest>> violations = hibernateValidator.validate(request);

            final String message = violations.iterator().next().getMessage(),
                    expectedMessage = "Invalid email!";

            final int size = violations.size(),
                    expectedSize = 1;

            assertEquals(expectedSize, size);
            assertEquals(expectedMessage, message);

        }

        @Test
        @DisplayName("Must have one violation when code is null")
        void givenUpdateForgotPassword_whenCodeIsNull_thenThrowConstraintViolationException() {

            final String email = "admin@gmail.com";
            final ForgotPasswordRequest request = new ForgotPasswordRequest(email, null);

            final Set<ConstraintViolation<ForgotPasswordRequest>> violations = hibernateValidator.validate(request);

            final String message = violations.iterator().next().getMessage(),
                    expectedMessage = "Required code!";

            final int size = violations.size(),
                    expectedSize = 1;

            assertEquals(expectedSize, size);
            assertEquals(expectedMessage, message);
        }

        @ParameterizedTest
        @ValueSource(strings = {"", " ", "   "})
        void givenUpdateForgotPassword_whenCodeIsInvalid_thenThrowConstraintViolationException(@SkipInject final String code) {

            final String email = "local@local.com";
            final ForgotPasswordRequest request = new ForgotPasswordRequest(email, code);

            final Set<ConstraintViolation<ForgotPasswordRequest>> violations = hibernateValidator.validate(request);

            final String message = violations.iterator().next().getMessage(),
                    expectedMessage = "Required code!";

            final int size = violations.size(),
                    expectedSize = 1;

            assertEquals(expectedSize, size);
            assertEquals(expectedMessage, message);
        }
    }

    @Nested
    @DisplayName("---- NewPasswordRequest tests ----")
    class NewPasswordRequestTests {

        @Test
        @DisplayName("Must have two violations when new password is null")
        void givenUpdateForgotPassword_whenNewPasswordIsNull_thenThrowConstraintViolationException() {

            final String confirmedNewPassword = "#Admin12";
            final NewPasswordRequest request = new NewPasswordRequest(null, confirmedNewPassword);

            final List<String> violationsMessages = hibernateValidator.validate(request)
                    .stream()
                    .map(ConstraintViolation::getMessage)
                    .toList();

            final String expectedEqualsPasswordMessage = "Different passwords: confirmed password should be equals to new password!";
            final String expectedNewPasswordMessage = "New password required!";

            final int expectedSize = 2;

            assertEquals(expectedSize, violationsMessages.size());
            assertTrue(violationsMessages.contains(expectedEqualsPasswordMessage));
            assertTrue(violationsMessages.contains(expectedNewPasswordMessage));
        }

        @Test
        @DisplayName("Must have two violations when confirmed new password is null")
        void givenUpdateForgotPassword_whenConfirmedNewPasswordIsNull_thenThrowConstraintViolationException() {

            final String newPassword = "#Admin12";
            final NewPasswordRequest request = new NewPasswordRequest(newPassword, null);

            final List<String> violationsMessages = hibernateValidator.validate(request)
                    .stream()
                    .map(ConstraintViolation::getMessage)
                    .toList();

            final String expectedEqualsPasswordMessage = "Different passwords: confirmed password should be equals to new password!";
            final String expectedNewPasswordMessage = "Confirmed new password required!";

            final int expectedSize = 2;

            assertEquals(expectedSize, violationsMessages.size());
            assertTrue(violationsMessages.contains(expectedEqualsPasswordMessage));
            assertTrue(violationsMessages.contains(expectedNewPasswordMessage));
        }


        @Test
        @DisplayName("Must have one violation when confirmed new password is different from new password")
        void givenUpdateForgotPassword_whenConfirmedNewPasswordIsDifferentFromNewPassword_thenThrowConstraintViolationException() {

            final String newPassword = "#Admin1";
            final String confirmedNewPassword = newPassword + "1";
            final NewPasswordRequest request = new NewPasswordRequest(newPassword, confirmedNewPassword);

            final Set<ConstraintViolation<NewPasswordRequest>> violations = hibernateValidator.validate(request);

            final String message = violations.iterator().next().getMessage(),
                    expectedMessage = "Different passwords: confirmed password should be equals to new password!";

            final int size = violations.size(),
                    expectedSize = 1;

            assertEquals(expectedSize, size);
            assertEquals(expectedMessage, message);

        }

        @ParameterizedTest
        @ValueSource(strings = {
                "abcde", "ABCDE", "12345", "abc123", "ABC123", "abc!@#", "123!@#", "abcABC", "abcABC123",
                "abcABC!@#", "123ABC!@#", "abc123!@#", "abcABC123!@#", "abcdE", "abcdE1",
                "abcdE1!@#", "abcdE1!@#2", "abcdE1!@#23", "abcdE1!@#234", "abcdE1!@#2345", "abcdE1!@#23456", "abcdE1!@#234567",
                "abcdE1!@#2345678", "abcdE1!@#23456789", "abcdE1!@#234567890", "abcdE1!@#2345678901"
        })
        @DisplayName("Must have one violation when new password and confirmed new password are invalid at pattern '5 and 8 characters, at least 1 special character and a capital letter'")
        void givenUpdateForgotPassword_whenPasswordIsInvalid_thenThrowConstraintViolationException(@SkipInject final String password) {

            final NewPasswordRequest request = new NewPasswordRequest(password, password);

            final Set<ConstraintViolation<NewPasswordRequest>> violations = hibernateValidator.validate(request);

            final String message = violations.iterator().next().getMessage(),
                    expectedMessage = "Invalid password! Between 5 and 8 characters, at least 1 special character and a capital letter!";

            final int size = violations.size(),
                    expectedSize = 2;

            assertEquals(expectedSize, size);
            assertEquals(expectedMessage, message);

        }
    }

    @Nested
    @DisplayName("---- UpdateForgotPassword tests ----")
    class UpdateForgotPasswordTests {

        ForgotPasswordRequest forgotPasswordRequest;

        NewPasswordRequest newPasswordRequest;

        @BeforeEach
        void setUp() {
            forgotPasswordRequest = new ForgotPasswordRequest("admin@admin.com", "8c878e6fff134a37-a208-7510c2638944");
            newPasswordRequest = new NewPasswordRequest("#Admin0", "#Admin0");
        }

        @Test
        @DisplayName("Should be throw NotAuthorizedException with status code 401 Unauthorized when credential not exists by email")
        void givenUpdateForgotPassword_whenCredentialNotExistsByEmail_thenThrowNotAuthorizedException() {

            when(credentialRepository.findCredentialByEmail(forgotPasswordRequest.email()))
                    .thenReturn(Optional.empty());

            final NotAuthorizedException e = assertThrows(NotAuthorizedException.class,
                    () -> service.updateForgotPassword(forgotPasswordRequest, newPasswordRequest));

            final String expectedMessage = "Account not exists!";
            final Response.Status expectedStatusCode = UNAUTHORIZED;

            assertEquals(expectedMessage, e.getMessage());
            assertEquals(expectedStatusCode, e.getResponse().getStatusInfo().toEnum());

            verify(credentialRepository).findCredentialByEmail(forgotPasswordRequest.email());
        }

        @Test
        @DisplayName("Should be throw WebApplicationException with status code 410 Gone when code is invalid or expired")
        void givenUpdateForgotPasswword_whenCodeIsInvalidOrExpired_thenThrowWebApplicationException() {

            when(credentialRepository.findCredentialByEmail(forgotPasswordRequest.email()))
                    .thenReturn(Optional.of(new Credential()));

            final WebApplicationException e = assertThrows(WebApplicationException.class,
                    () -> service.updateForgotPassword(forgotPasswordRequest, newPasswordRequest));

            final String expectedMessage = "Invalid or expired code, use a valid code or request a new forgot password email on login page!";
            final Response.Status expectedStatusCode = GONE;

            assertEquals(expectedMessage, e.getMessage());
            assertEquals(expectedStatusCode, e.getResponse().getStatusInfo().toEnum());

            verify(credentialRepository).findCredentialByEmail(forgotPasswordRequest.email());
        }

        @Test
        @DisplayName("Should be throw WebApplicationException with status code 409 Conflict when the new password is equals to the current password")
        void givenUpdateForgotPasswword_whenNewPasswordIsEqualsToCurrentPassword_thenThrowWebApplicationException() {

            final String encryptedPassword = CryptographyUtils.encrypt(newPasswordRequest.newPassword());

            final Credential credential = Credential.builder()
                    .id(UUID.randomUUID())
                    .email(forgotPasswordRequest.email())
                    .password(encryptedPassword)
                    .build();

            when(credentialRepository.findCredentialByEmail(forgotPasswordRequest.email()))
                    .thenReturn(Optional.of(credential));

            when(forgotPasswordRepository.findByIdOptional(forgotPasswordRequest.code()))
                    .thenReturn(Optional.of(new ForgotPassword(credential.getId())));

            final WebApplicationException e = assertThrows(WebApplicationException.class,
                    () -> service.updateForgotPassword(forgotPasswordRequest, newPasswordRequest));

            final String expectedMessage = "New password can´t be equals to the current password!";
            final Response.Status expectedStatusCode = CONFLICT;

            assertEquals(expectedMessage, e.getMessage());
            assertEquals(expectedStatusCode, e.getResponse().getStatusInfo().toEnum());

            verify(credentialRepository).findCredentialByEmail(forgotPasswordRequest.email());
            verify(forgotPasswordRepository).findByIdOptional(forgotPasswordRequest.code());
        }

        @Test
        @DisplayName("Should update and return encryptedPassword when request is valid")
        void givenUpdateForgotPassword_whenRequestIsValid_thenPersistAndReturnEncryptedPassword() {

            final String credentialPassword = CryptographyUtils.encrypt(
                    newPasswordRequest + "!"
            );

            final Credential credential = Credential.builder()
                    .id(UUID.randomUUID())
                    .email(forgotPasswordRequest.email())
                    .password((credentialPassword))
                    .build();

            when(credentialRepository.findCredentialByEmail(forgotPasswordRequest.email()))
                    .thenReturn(Optional.of(credential));

            when(forgotPasswordRepository.findByIdOptional(forgotPasswordRequest.code()))
                    .thenReturn(Optional.of(new ForgotPassword(credential.getId())));

            doNothing().when(forgotPasswordRepository)
                    .delete(any(ForgotPassword.class));

            doNothing().when(credentialRepository)
                    .persist(credential);

            final String expectedPassword = CryptographyUtils.encrypt(newPasswordRequest.newPassword());

            try (MockedStatic<CryptographyUtils> mockedStatic = mockStatic(CryptographyUtils.class)) {

                mockedStatic.when(() -> CryptographyUtils.encrypt(any(String.class)))
                        .thenReturn(expectedPassword);

                final String encryptedPassword = service.updateForgotPassword(forgotPasswordRequest, newPasswordRequest);

                assertEquals(expectedPassword, encryptedPassword);

                mockedStatic.verify(() -> CryptographyUtils.encrypt(any(String.class)));
                verify(credentialRepository).findCredentialByEmail(forgotPasswordRequest.email());
                verify(forgotPasswordRepository).findByIdOptional(forgotPasswordRequest.code());
                verify(forgotPasswordRepository).delete(any(ForgotPassword.class));
                verify(credentialRepository).persist(credential);
            }
        }
    }

    @Nested
    @DisplayName("---- UpdatePasswordRequest tests ----")
    class UpdatePasswordRequestTests {

        String newPassword = "#Admin1";

        @Test
        @DisplayName("Must have one violation when the current password is null")
        void givenUpdateCurrentPassword_thenCurrentPasswordIsNull_thenThrowConstraintViolationException() {

            final UpdatePasswordRequest request = new UpdatePasswordRequest(
                    null, new NewPasswordRequest(newPassword, newPassword)
            );

            final Set<ConstraintViolation<UpdatePasswordRequest>> violations = hibernateValidator.validate(request);

            final String message = violations.iterator().next().getMessage(),
                    expectedMessage = "Current password required!";

            final int size = violations.size(),
                    expectedSize = 1;

            assertEquals(expectedSize, size);
            assertEquals(expectedMessage, message);

        }


        @ParameterizedTest
        @ValueSource(strings = {
                "abcde", "ABCDE", "12345", "abc123", "ABC123", "abc!@#", "123!@#", "abcABC", "abcABC123",
                "abcABC!@#", "123ABC!@#", "abc123!@#", "abcABC123!@#", "abcdE", "abcdE1",
                "abcdE1!@#", "abcdE1!@#2", "abcdE1!@#23", "abcdE1!@#234", "abcdE1!@#2345", "abcdE1!@#23456", "abcdE1!@#234567",
                "abcdE1!@#2345678", "abcdE1!@#23456789", "abcdE1!@#234567890", "abcdE1!@#2345678901"
        })
        @DisplayName("Must have one violation when the current password is invalid at pattern '5 and 8 characters, at least 1 special character and a capital letter'")
        void givenUpdateCurrentPassword_whenCurrentPasswordIsInvalid_thenThrowConstraintViolationException(@SkipInject final String currentPassword) {


            final UpdatePasswordRequest request = new UpdatePasswordRequest(
                    currentPassword, new NewPasswordRequest(newPassword, newPassword)
            );

            final Set<ConstraintViolation<UpdatePasswordRequest>> violations = hibernateValidator.validate(request);

            final String message = violations.iterator().next().getMessage(),
                    expectedMessage = "Invalid password! Between 5 and 8 characters, at least 1 special character and a capital letter!";

            final int size = violations.size(),
                    expectedSize = 1;

            assertEquals(expectedSize, size);
            assertEquals(expectedMessage, message);

        }

        @Test
        @DisplayName("Must have one violation when the new password request is null")
        void givenUpdateCurrentPassword_whenNewPasswordRequestIsNull_thenThrowConstraintViolationException() {

            final UpdatePasswordRequest request = new UpdatePasswordRequest(
                    newPassword, null
            );

            final Set<ConstraintViolation<UpdatePasswordRequest>> violations = hibernateValidator.validate(request);

            final String message = violations.iterator().next().getMessage(),
                    expectedMessage = "New passwords required!";

            final int size = violations.size(),
                    expectedSize = 1;

            assertEquals(expectedSize, size);
            assertEquals(expectedMessage, message);

        }
    }

    @Nested
    @DisplayName("---- UpdateCurrentPassword tests ----")
    class UpdateCurrentPasswordTests {

        String email;

        UpdatePasswordRequest request;

        @BeforeEach
        void setUp() {
            email = "admin@admin.com";

            final String currentPassword = "#Admin1";
            final String newPassword = "#Admin2";

            request = new UpdatePasswordRequest(currentPassword, new NewPasswordRequest(newPassword, newPassword));
        }

        @Test
        @DisplayName("Should be throw ForbiddenException with code 403 Forbidden when the current password is different to credential password")
        void givenUpdateCurrentPassword_whenCurrentPasswordIsDifferentToCredentialPassword_thenThrowForbiddenException() {

            final String credentialPassword = CryptographyUtils.encrypt(
                    request.currentPassword() + "%"
            );

            final Credential credential = Credential.builder()
                    .email(email)
                    .password(credentialPassword)
                    .build();

            when(credentialRepository.findCredentialByEmail(email))
                    .thenReturn(Optional.of(credential));

            final ForbiddenException e = assertThrows(ForbiddenException.class,
                    () -> service.updateCurrentPassword(email, request));

            final String expectedMessage = "Invalid current password, if you don´t remember go to 'forgot password' on the login page!";
            final Response.Status expectedStatusCode = FORBIDDEN;

            assertEquals(expectedMessage, e.getMessage());
            assertEquals(expectedStatusCode, e.getResponse().getStatusInfo().toEnum());

            verify(credentialRepository).findCredentialByEmail(email);
        }

        @Test
        @DisplayName("Should be throw WebApplicationException with code 409 Conflict when the new password is equals to credential password")
        void givenUpdateCurrentPassword_whenNewPasswordIsEqualsToCredentialPassword_thenThrowWebApplicationException() {

            final String credentialPassword = CryptographyUtils.encrypt(request.newPasswordRequest().newPassword());

            final Credential credential = Credential.builder()
                    .email(email)
                    .password(credentialPassword)
                    .build();

            when(credentialRepository.findCredentialByEmail(email))
                    .thenReturn(Optional.of(credential));

            try (MockedStatic<CryptographyUtils> mockedStatic = mockStatic(CryptographyUtils.class)) {

                mockedStatic.when(() -> CryptographyUtils.equals(request.currentPassword(), credential.getPassword()))
                        .thenReturn(true);

                mockedStatic.when(() -> CryptographyUtils.equals(request.newPasswordRequest().newPassword(), credential.getPassword()))
                        .thenReturn(true);

                final WebApplicationException e = assertThrows(WebApplicationException.class,
                        () -> service.updateCurrentPassword(email, request));

                final String expectedMessage = "New password can´t be equals to the current password!";
                final Response.Status expectedStatusCode = CONFLICT;

                assertEquals(expectedMessage, e.getMessage());
                assertEquals(expectedStatusCode, e.getResponse().getStatusInfo().toEnum());

                verify(credentialRepository).findCredentialByEmail(email);
                mockedStatic.verify(
                        () -> CryptographyUtils.equals(anyString(), anyString()),
                        times(2)
                );
            }

        }

        @Test
        @DisplayName("Should update and return encryptedPassword when request is valid")
        void givenUpdateCurrentPassword_whenRequestIsValid_thenPersistAndReturnEncryptedPassword() {

            final String currentPassword = request.currentPassword();
            final String credentialPassword = CryptographyUtils.encrypt(currentPassword);

            final Credential credential = Credential.builder()
                    .email(email)
                    .password(credentialPassword)
                    .build();

            when(credentialRepository.findCredentialByEmail(email))
                    .thenReturn(Optional.of(credential));

            final String newPassword = request.newPasswordRequest().newPassword();
            final String expectedPassword = CryptographyUtils.encrypt(newPassword);

            try (MockedStatic<CryptographyUtils> mockedStatic = mockStatic(CryptographyUtils.class)) {

                mockedStatic.when(() -> CryptographyUtils.equals(currentPassword, credential.getPassword()))
                        .thenReturn(true);

                mockedStatic.when(() -> CryptographyUtils.equals(newPassword, credential.getPassword()))
                        .thenReturn(false);

                mockedStatic.when(() -> CryptographyUtils.encrypt(newPassword))
                        .thenReturn(expectedPassword);

                doNothing().when(credentialRepository).persist(credential);

                final String encryptedPassword = service.updateCurrentPassword(email, request);

                assertEquals(expectedPassword, encryptedPassword);

                verify(credentialRepository).findCredentialByEmail(email);
                verify(credentialRepository).persist(credential);

                mockedStatic.verify(
                        () -> CryptographyUtils.equals(anyString(), anyString()),
                        times(2)
                );

                mockedStatic.verify(() -> CryptographyUtils.encrypt(newPassword));
            }
        }
    }

    @Nested
    @DisplayName("---- DeleteCredentialRequest tests ---- ")
    class DeleteCredentialRequestTests {

        @ParameterizedTest
        @ValueSource(strings = {
                "", " ", "   ", "a", "12345", "invalid-uuid", "550e8400e29b41d4a716446655440000", "550e8400-e29b-41d4-a716-44665544000",
                "550e8400-e29b-41d4-a716-4466554400000", "550e8400-e29b-41d4-a716-44665544zzzz", "550e8400-e29b-41d4-a716-44665544****",
                "550e8400-e29b-41d4-a716-44665544@!#$", "550e8400-e29b-41d4-a716-44665544abcd1234", "550e8400-e29b-41d4-a716-44665544",
                "550e8400e29b41d4a7164466554400000000", "550e8400-e29b-41d4-a716-44665544-0000", "550e8400-e29b-41d4-a716-44665544-",
                "550e8400-e29b-41d4-a716-44665544-****", "550e8400-e29b-41d4-a716-44665544-zzzz", "550e8400-e29b-41d4-a716-44665544-1234",
                "550e8400-e29b-41d4-a716-44665544-@!#$", "550e8400-e29b-41d4-a716-44665544-000000", "550e8400-e29b-41d4-a716-44665544-",
                "550e8400-e29b-41d4-a716-44665544-zzzzzz", "550e8400-e29b-41d4-a716-44665544-****", "550e8400-e29b-41d4-a716-44665544-",
                "550e8400-e29b-41d4-a716-44665544-123456", "550e8400-e29b-41d4-a716-44665544-@!#$", "550e8400-e29b-41d4-a716-44665544-000000",
                "550e8400-e29b-41d4-a716-44665544-zzzzzz", "550e8400-e29b-41d4-a716-44665544-****", "550e8400-e29b-41d4-a716-44665544-",
                "550e8400-e29b-41d4-a716-44665544-123456", "550e8400-e29b-41d4-a716-44665544-@!#$", "550e8400-e29b-41d4-a716-44665544-000000",
                "550e8400-e29b-41d4-a716-44665544-zzzzzz", "550e8400-e29b-41d4-a716-44665544-****", "550e8400-e29b-41d4-a716-44665544-",
                "550e8400-e29b-41d4-a716-44665544-123456", "550e8400-e29b-41d4-a716-44665544-@!#$", "550e8400-e29b-41d4-a716-44665544-000000",
                "550e8400-e29b-41d4-a716-44665544-zzzzzz", "550e8400-e29b-41d4-a716-44665544-****", "550e8400-e29b-41d4-a716-44665544-",
                "550e8400-e29b-41d4-a716-44665544-123456", "550e8400-e29b-41d4-a716-44665544-@!#$", "550e8400-e29b-41d4-a716-44665544-000000",
                "550e8400-e29b-41d4-a716-44665544-zzzzzz", "550e8400-e29b-41d4-a716-44665544-****", "550e8400-e29b-41d4-a716-44665544-",
                "550e8400-e29b-41d4-a716-44665544-123456", "550e8400-e29b-41d4-a716-44665544-@!#$", "550e8400-e29b-41d4-a716-44665544-000000",
                "550e8400-e29b-41d4-a716-44665544-zzzzzz", "550e8400-e29b-41d4-a716-44665544-****", "550e8400-e29b-41d4-a716-44665544-",
                "550e8400-e29b-41d4-a716-44665544-123456", "550e8400-e29b-41d4-a716-44665544-@!#$", "550e8400-e29b-41d4-a716-44665544-000000"
        })
        @DisplayName("Must have one violation when id is invalid")
        void givenDeleteCredential_whenIdIsInvalid_thenThrowConstraintViolationException(@SkipInject final String id) {

            final DeleteCredentialRequest request = new DeleteCredentialRequest(id);

            final Set<ConstraintViolation<DeleteCredentialRequest>> violations = hibernateValidator.validate(request);

            final String message = violations.iterator().next().getMessage(),
                    expectedMessage = "Invalid id!";

            final int size = violations.size(),
                    expectedSize = 1;

            assertEquals(expectedSize, size);
            assertEquals(expectedMessage, message);
        }

        @ParameterizedTest
        @ValueSource(strings = {
                "550e8400-e29b-41d4-a716-446655440000",
                "123e4567-e89b-12d3-a456-426614174000",
                "9f8e7d6c-5b4a-3c2d-1e0f-123456789abc"
        })
        @DisplayName("Should do nothing when the id is valid")
        void givenDeleteCredential_whenIdIsValid_thenDoNothing(@SkipInject final String id) {
            final DeleteCredentialRequest request = new DeleteCredentialRequest(id);
            assertTrue(
                    hibernateValidator.validate(request).isEmpty()
            );
        }
    }

    @Nested
    @DisplayName("---- DeleteCredential tests ----")
    class DeleteCredentialTests {

        DeleteCredentialRequest request;

        UUID id;

        @BeforeEach
        void setUp() {
            request = new DeleteCredentialRequest("8c878e6f-ff13-4a37-a208-7510c2638944");
            id = UUID.fromString(request.id());
        }

        @Test
        @DisplayName("Should be throw NotFountException with status code 404 NotFound when the credential not exists")
        void givenDeleteCredential_whenCredentialNotExists_thenThrowNotFoundException() {

            when(credentialRepository.deleteById(id))
                    .thenReturn(false);

            final NotFoundException e = assertThrows(NotFoundException.class,
                    () -> service.deleteCredential(request));

            final String expectedMessage = "Account not exists!";
            final Response.Status expectedStatusCode = NOT_FOUND;

            assertEquals(expectedMessage, e.getMessage());
            assertEquals(expectedStatusCode, e.getResponse().getStatusInfo().toEnum());

            verify(credentialRepository).deleteById(id);
        }


        @Test
        @DisplayName("Should does not throw NotFoundException when credential exists")
        void givenDeleteCredential_whenCredentialExists_thenDoesNotThrowNotFoundException() {

            when(credentialRepository.deleteById(id))
                    .thenReturn(true);

            assertDoesNotThrow(() -> service.deleteCredential(request));

            verify(credentialRepository).deleteById(id);
        }

    }


}

