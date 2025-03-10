package dev.rayan.model;

import io.quarkus.mongodb.panache.common.MongoEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.bson.codecs.pojo.annotations.BsonCreator;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.codecs.pojo.annotations.BsonProperty;

import java.time.LocalDateTime;
import java.util.UUID;

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Getter
@Setter
@MongoEntity(collection = "forgot_password_requests")
public class ForgotPassword {

    @BsonId
    String code;
    UUID credentialId;
    LocalDateTime madeAt;

    //Serialize
    public ForgotPassword(UUID credentialId) {
        this.code = UUID.randomUUID()
                .toString()
                .replace("-", "");

        this.credentialId = credentialId;
        this.madeAt = LocalDateTime.now();
    }

    //Deserialize
    @BsonCreator
    public ForgotPassword(@BsonProperty("code") String code,
                          @BsonProperty("credentialId") UUID credentialId,
                          @BsonProperty("madeAt") LocalDateTime madeAt) {
        this.code = code;
        this.credentialId = credentialId;
        this.madeAt = madeAt;
    }
}

