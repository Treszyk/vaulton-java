package dev.vaulton.vaultonapi.application.dto.auth.requests;

import dev.vaulton.vaultonapi.application.dto.shared.EncryptedValueDto;

import java.util.UUID;
import com.fasterxml.jackson.annotation.JsonProperty;

public record RegisterRequest(
        @JsonProperty("AccountId") UUID accountId,
        @JsonProperty("Verifier") byte[] verifier,
        @JsonProperty("AdminVerifier") byte[] adminVerifier,
        @JsonProperty("RkVerifier") byte[] rkVerifier,
        @JsonProperty("S_Pwd") byte[] s_pwd,
        @JsonProperty("KdfMode") int kdfMode,
        @JsonProperty("MKWrapPwd") EncryptedValueDto mkWrapPwd,
        @JsonProperty("MKWrapRk") EncryptedValueDto mkWrapRk,
        @JsonProperty("CryptoSchemaVer") int cryptoSchemaVer
) {}
