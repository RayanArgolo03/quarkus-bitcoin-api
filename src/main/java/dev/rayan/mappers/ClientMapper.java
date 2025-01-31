package dev.rayan.mappers;

import dev.rayan.dto.respose.ClientResponse;
import dev.rayan.model.client.Client;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "jakarta-cdi")
public interface ClientMapper {

    @Mapping(target = "birthDate", dateFormat = "dd/MM/yyyy")
    @Mapping(target = "createdAt", dateFormat = "dd/MM/yyyy HH:mm")
    @Mapping(target = "updatedAt", dateFormat = "dd/MM/yyyy HH:mm")
    ClientResponse clientToResponse(Client client);

}
