package dev.rayan.mappers;

import dev.rayan.dto.response.client.ForgotPasswordResponse;
import dev.rayan.model.ForgotPassword;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import static org.mapstruct.MappingConstants.ComponentModel.JAKARTA_CDI;
import static org.mapstruct.ReportingPolicy.ERROR;

@Mapper(componentModel = JAKARTA_CDI, unmappedTargetPolicy = ERROR)
public interface ForgotPasswordMapper {
    @Mapping(target = "code", source = "code")
    @Mapping(target = "madeAt", source = "madeAt")
    ForgotPasswordResponse forgotPasswordToResponse(ForgotPassword forgotPassword);
}
