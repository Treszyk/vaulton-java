package dev.vaulton.vaultonapi.application.dto.auth.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.UUID;

public record PreRegisterResponse(
    @JsonProperty("AccountId") UUID accountId,
    @JsonProperty("CryptoSchemaVer") int cryptoSchemaVer) {}
