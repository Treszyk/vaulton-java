package dev.vaulton.vaultonapi.application.dto.shared;

import com.fasterxml.jackson.annotation.JsonProperty;

public record EncryptedValueDto(
        @JsonProperty("Nonce") byte[] nonce,
        @JsonProperty("CipherText") byte[] cipherText,
        @JsonProperty("Tag") byte[] tag
) {}
