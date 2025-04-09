package dev.rayan.mappers;

import dev.rayan.dto.request.client.CreateClientRequest;
import dev.rayan.dto.response.client.ClientResponse;
import dev.rayan.model.Address;
import dev.rayan.model.Client;
import dev.rayan.model.Credential;
import org.apache.commons.text.StringSubstitutor;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.Map;

import static org.mapstruct.MappingConstants.ComponentModel.JAKARTA_CDI;
import static org.mapstruct.ReportingPolicy.ERROR;

@Mapper(componentModel = JAKARTA_CDI, unmappedTargetPolicy = ERROR, uses = CredentialMapper.class)
public interface ClientMapper {

    @Named(value = "formatCompleteAddress")
    default String formatCompleteAddress(final Address address) {

        final String format = "${cep}, ${state}, ${neighbourhood}, ${street}, ${houseNumber}";

        final Map<String, String> params = Map.of(
                "cep", address.getCep(),
                "state", address.getState(),
                "neighbourhood", address.getNeighbourhood(),
                "street", address.getStreet(),
                "houseNumber", address.getHouseNumber()
        );

        return StringSubstitutor.replace(format, params);
    }

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "credential", source = "credential")
    @Mapping(target = "address", source = "request.address")
    @Mapping(target = "transactions", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Client requestToClient(Credential credential, CreateClientRequest request);

    //uses the CredentialMapper here, credentialToResponse method
    @Mapping(target = "credential", source = "credential")

    @Mapping(target = "credential.id", ignore = true)
    @Mapping(target = "credential.keycloakUserId", ignore = true)
    @Mapping(target = "completeAddress", source = "client.address", qualifiedByName = "formatCompleteAddress")
    ClientResponse clientToResponse(Client client);

}
