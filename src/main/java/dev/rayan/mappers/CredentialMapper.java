package dev.rayan.mappers;

import dev.rayan.dto.request.authentication.CredentialRequest;
import dev.rayan.dto.response.client.CredentialResponse;
import dev.rayan.model.Credential;
import dev.rayan.utils.CryptographyUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "jakarta-cdi", imports = CryptographyUtils.class)
public interface CredentialMapper {

    @Mapping(target = "keycloakUserId", ignore = true)
    CredentialResponse credentialToResponse(Credential credential);

    @Mapping(target = "password", expression = "java(CryptographyUtils.encrypt(request.password()))")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Credential requestToCredential(CredentialRequest request);
}
