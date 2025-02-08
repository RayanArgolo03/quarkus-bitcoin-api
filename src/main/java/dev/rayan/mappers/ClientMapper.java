package dev.rayan.mappers;

import dev.rayan.dto.request.client.CreateClientRequest;
import dev.rayan.dto.response.client.CreatedClientResponse;
import dev.rayan.model.Client;
import dev.rayan.model.Credential;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "jakarta-cdi")
public interface ClientMapper {

    CreatedClientResponse clientToResponse(Client client);

    @Mapping(target = "credential", source = "credential")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "transactions", ignore = true)
    Client requestToClient(Credential credential, CreateClientRequest request);

}
