package dev.rayan.mappers;

import dev.rayan.dto.respose.ClientResponse;
import dev.rayan.model.client.Client;
import org.mapstruct.Mapper;

@Mapper(componentModel = "jakarta-cdi")
public interface ClientMapper {

    ClientResponse clientToResponse(Client client);

}
