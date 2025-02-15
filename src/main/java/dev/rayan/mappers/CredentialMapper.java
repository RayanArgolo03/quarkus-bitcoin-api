package dev.rayan.mappers;

import dev.rayan.dto.request.client.CreateCredentialRequest;
import dev.rayan.dto.response.client.CredentialResponse;
import dev.rayan.model.Credential;
import io.quarkus.elytron.security.common.BcryptUtil;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "jakarta-cdi", imports = BcryptUtil.class)
public interface CredentialMapper {

    @Mapping(target = "keycloakUserId", ignore = true)
    CredentialResponse credentialToResponse(Credential credential);

    @Mapping(target = "password", expression = "java(BcryptUtil.bcryptHash(request.password()))")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Credential requestToCredential(CreateCredentialRequest request);
}
