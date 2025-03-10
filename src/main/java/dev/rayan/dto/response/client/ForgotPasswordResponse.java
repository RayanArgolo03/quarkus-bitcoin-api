package dev.rayan.dto.response.client;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.bson.codecs.pojo.annotations.BsonProperty;

import java.time.LocalDateTime;

public record ForgotPasswordResponse(
        String code,
        @BsonProperty("madeAt")
        @JsonFormat(pattern = "dd/MM/yyyy HH:mm")
        LocalDateTime madeAt
) {
}
