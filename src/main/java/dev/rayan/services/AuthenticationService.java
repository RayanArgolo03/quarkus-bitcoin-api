package dev.rayan.services;

import dev.rayan.dto.request.authentication.CreateCredentialRequest;
import dev.rayan.dto.request.authentication.ForgotPasswordRequest;
import dev.rayan.dto.response.client.CredentialResponse;
import dev.rayan.dto.response.client.ForgotPasswordResponse;
import dev.rayan.exceptions.BusinessException;
import dev.rayan.mappers.CredentialMapper;
import dev.rayan.mappers.ForgotPasswordMapper;
import dev.rayan.model.Credential;
import dev.rayan.model.ForgotPassword;
import dev.rayan.repositories.CredentialRepository;
import dev.rayan.repositories.ForgotPasswordRepository;
import dev.rayan.scheduler.ExpiredForgotPasswordScheduler;
import dev.rayan.utils.CryptographyUtils;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.ForbiddenException;
import jakarta.ws.rs.NotAuthorizedException;

import java.util.Optional;

import static jakarta.ws.rs.core.Response.Status.UNAUTHORIZED;
import static java.lang.String.format;

@ApplicationScoped
public final class AuthenticationService {

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

    public CredentialResponse persistCredential(final CreateCredentialRequest request) {

        if (findCredentialByEmail(request.email()).isPresent()) {
            throw new NotAuthorizedException(format("Client with email %s already exists!", request.email()), UNAUTHORIZED);
        }

        final Credential credential = credentialMapper.requestToCredential(request);
        credentialRepository.persist(credential);

        return credentialMapper.credentialToResponse(credential);
    }

    public CredentialResponse login(final CreateCredentialRequest request) {

        final Credential credential = findCredentialByEmail(request.email())
                .orElseThrow(() -> new NotAuthorizedException("Use a valid email!", UNAUTHORIZED));

        if (!CryptographyUtils.matches(request.password(), credential.getPassword())) {
            throw new NotAuthorizedException("Use a valid password!", UNAUTHORIZED);
        }

        return credentialMapper.credentialToResponse(credential);
    }

    public ForgotPasswordResponse persistForgotPassword(final String email) {

        final Credential credential = findCredentialByEmail(email)
                .orElseThrow(() -> new NotAuthorizedException("Use a valid email!", UNAUTHORIZED));

        final ForgotPassword forgotPassword = new ForgotPassword(credential.getId());
        forgotPasswordRepository.persist(forgotPassword);

        return forgotPasswordMapper.forgotPasswordToResponse(forgotPassword);
    }


    public Credential validateForgotPassword(final ForgotPasswordRequest forgotRequest, final String newPassword) {

        final Credential credential = findCredentialByEmail(forgotRequest.getEmail())
                .orElseThrow(() -> new NotAuthorizedException("Use a valid email!", UNAUTHORIZED));

        final ForgotPassword forgotPassword = forgotPasswordRepository.findByIdOptional(forgotRequest.getCode())
                .orElseThrow(() -> new ForbiddenException("Invalid or expired code, use a valid code or request a new forgot password email on login page! "));

        if (CryptographyUtils.matches(newPassword, credential.getPassword())) {
            throw new BusinessException("New password can´t be equals to the current password!");
        }

        forgotPasswordRepository.deleteForgotPassword(forgotPassword.getCode());

        return credential;
    }

    public void updatePassword(final Credential credential, final String password) {
        credential.setPassword(
                CryptographyUtils.encrypt(password)
        );
        credentialRepository.persist(credential);
    }

    public Optional<Credential> findCredentialByEmail(final String email) {
        return credentialRepository.findCredentialByEmail(email);
    }

}
