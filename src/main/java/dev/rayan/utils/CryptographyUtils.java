package dev.rayan.utils;

import io.quarkus.elytron.security.common.BcryptUtil;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CryptographyUtils {

    public static String encrypt(final String value) {
        return BcryptUtil.bcryptHash(value);
    }

    public static boolean equals(final String requestValue, final String encryptedValue) { return BcryptUtil.matches(requestValue, encryptedValue); }
}
