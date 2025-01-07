package dev.rayan.mappers;

import dev.rayan.dto.respose.CredentialResponse;
import dev.rayan.model.client.Credential;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "jakarta-cdi")
public interface CredentialMapper {

    @Mapping(target = "keycloakUserId", ignore = true)
    CredentialResponse credentialToResponse(Credential credential);
}
