package dev.rayan.mappers;

import dev.rayan.dto.response.client.ForgotPasswordResponse;
import dev.rayan.model.ForgotPassword;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.UUID;

@Mapper(componentModel = "jakarta-cdi")
public interface ForgotPasswordMapper {
    ForgotPasswordResponse forgotPasswordToResponse(ForgotPassword forgotPassword);
}
