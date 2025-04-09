package dev.rayan.mappers;

import dev.rayan.dto.request.authentication.CredentialRequest;
import dev.rayan.dto.response.client.CredentialResponse;
import dev.rayan.model.Credential;
import dev.rayan.utils.CryptographyUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import static org.mapstruct.MappingConstants.ComponentModel.JAKARTA_CDI;
import static org.mapstruct.ReportingPolicy.ERROR;

@Mapper(componentModel = JAKARTA_CDI, unmappedTargetPolicy = ERROR, imports = CryptographyUtils.class )
public interface CredentialMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "email", source = "emailRequest.email")
    @Mapping(target = "password", expression = "java(CryptographyUtils.encrypt(request.password()))")
    @Mapping(target = "updatedAt", ignore = true)
    Credential requestToCredential(CredentialRequest request);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "email", source = "email")
    @Mapping(target = "password", source = "password")
    @Mapping(target = "createdAt", source = "createdAt")
    @Mapping(target = "keycloakUserId", ignore = true)
    CredentialResponse credentialToResponse(Credential credential);
}
