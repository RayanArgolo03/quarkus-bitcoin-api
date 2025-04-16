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
import dev.rayan.scheduler.ExpiredForgotPasswordScheduler;
import dev.rayan.utils.CryptographyUtils;
import io.quarkus.arc.Unremovable;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.ForbiddenException;
import jakarta.ws.rs.NotAuthorizedException;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.WebApplicationException;

import java.util.Optional;
import java.util.UUID;

import static jakarta.ws.rs.core.Response.Status.*;
import static java.lang.String.format;

@Unremovable
@ApplicationScoped
public class AuthenticationService {

    private static final String INVALID_CREDENTIAL_MESSAGE = "Account not exists!";

    @Inject
    CredentialRepository credentialRepository;

    @Inject
    CredentialMapper credentialMapper;

    @Inject
    ForgotPasswordRepository forgotPasswordRepository;

    @Inject
    ForgotPasswordMapper forgotPasswordMapper;

    @Inject
    ExpiredForgotPasswordScheduler expiredForgotPasswordScheduler;

    public CredentialResponse persistCredential(final CredentialRequest request) {

        if (findCredentialByEmail(request.emailRequest().email()).isPresent()) {
            throw new WebApplicationException(format("Client with email %s already exists!", request.emailRequest().email()), CONFLICT);
        }

        final Credential credential = credentialMapper.requestToCredential(request);
        credentialRepository.persist(credential);

        return credentialMapper.credentialToResponse(credential);
    }

    public CredentialResponse login(final CredentialRequest request) {

        final Credential credential = findCredentialByEmail(request.emailRequest().email())
                .orElseThrow(() -> new NotAuthorizedException(INVALID_CREDENTIAL_MESSAGE, UNAUTHORIZED));

        if (!CryptographyUtils.equals(request.password(), credential.getPassword())) {
            throw new NotAuthorizedException("Use a valid password!", UNAUTHORIZED);
        }

        return credentialMapper.credentialToResponse(credential);
    }

    public ForgotPasswordResponse persistForgotPassword(final EmailRequest request) {

        final Credential credential = findCredentialByEmail(request.email())
                .orElseThrow(() -> new NotAuthorizedException(INVALID_CREDENTIAL_MESSAGE, UNAUTHORIZED));

        final ForgotPassword forgotPassword = new ForgotPassword(credential.getId());
        forgotPasswordRepository.persist(forgotPassword);

        return forgotPasswordMapper.forgotPasswordToResponse(forgotPassword);
    }

    public String updateForgotPassword(final ForgotPasswordRequest forgotRequest, final NewPasswordRequest newPasswordRequest) {

        final Credential credential = findCredentialByEmail(forgotRequest.email())
                .orElseThrow(() -> new NotAuthorizedException(INVALID_CREDENTIAL_MESSAGE, UNAUTHORIZED));

        //if expired will be deleted by scheduler
        final ForgotPassword forgotPassword = forgotPasswordRepository.findByIdOptional(forgotRequest.code())
                .orElseThrow(() -> new WebApplicationException("Invalid or expired code, use a valid code or request a new forgot password email on login page!", GONE));

        final String newPassword = newPasswordRequest.newPassword();

        if (CryptographyUtils.equals(newPassword, credential.getPassword())) {
            throw new WebApplicationException("New password can´t be equals to the current password!", CONFLICT);
        }

        forgotPasswordRepository.delete(forgotPassword);
        updatePassword(credential, newPassword);

        return credential.getPassword();
    }

    public String updateCurrentPassword(final String email, final UpdatePasswordRequest request) {

        //Credential has been found in Keycloak, get() is secure
        final Credential credential = findCredentialByEmail(email)
                .get();

        if (!CryptographyUtils.equals(request.currentPassword(), credential.getPassword())) {
            throw new ForbiddenException("Invalid current password, if you don´t remember go to 'forgot password' on the login page!");
        }

        final String newPassword = request.newPasswordRequest().newPassword();

        if (CryptographyUtils.equals(newPassword, credential.getPassword())) {
            throw new WebApplicationException("New password can´t be equals to the current password!", CONFLICT);
        }

        updatePassword(credential, newPassword);

        return credential.getPassword();
    }

    private void updatePassword(final Credential credential, final String newPassword) {
        credential.setPassword(CryptographyUtils.encrypt(newPassword));
        credentialRepository.persist(credential);
    }

    public Optional<Credential> findCredentialByEmail(final String email) {
        return credentialRepository.findCredentialByEmail(email);
    }

    public void deleteCredential(final DeleteCredentialRequest request) {

        final boolean deleted = credentialRepository.deleteById(
                UUID.fromString(request.id())
        );

        if (!deleted) throw new NotFoundException(INVALID_CREDENTIAL_MESSAGE);

    }
}
