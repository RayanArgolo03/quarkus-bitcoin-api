package dev.rayan.mappers;

import dev.rayan.dto.respose.CredentialResponse;
import dev.rayan.model.client.Credential;
import org.mapstruct.Mapper;

@Mapper(componentModel = "jakarta-cdi")
public interface CredentialMapper {
    CredentialResponse credentialToResponse(Credential credential);
}
