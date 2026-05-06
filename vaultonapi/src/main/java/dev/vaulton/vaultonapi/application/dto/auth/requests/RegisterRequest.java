package dev.vaulton.vaultonapi.application.dto.auth.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import dev.vaulton.vaultonapi.application.dto.shared.EncryptedValueDto;
import java.util.Arrays;
import java.util.UUID;

public record RegisterRequest(
    @JsonProperty("AccountId") UUID accountId,
    @JsonProperty("Verifier") byte[] verifier,
    @JsonProperty("AdminVerifier") byte[] adminVerifier,
    @JsonProperty("RkVerifier") byte[] rkVerifier,
    @JsonProperty("S_Pwd") byte[] s_pwd,
    @JsonProperty("KdfMode") int kdfMode,
    @JsonProperty("MKWrapPwd") EncryptedValueDto mkWrapPwd,
    @JsonProperty("MKWrapRk") EncryptedValueDto mkWrapRk,
    @JsonProperty("CryptoSchemaVer") int cryptoSchemaVer)
    implements AutoCloseable {
  public void wipe() {
    if (verifier != null) Arrays.fill(verifier, (byte) 0);
    if (adminVerifier != null) Arrays.fill(adminVerifier, (byte) 0);
    if (rkVerifier != null) Arrays.fill(rkVerifier, (byte) 0);
    if (s_pwd != null) Arrays.fill(s_pwd, (byte) 0);

    if (mkWrapPwd != null) mkWrapPwd.wipe();
    if (mkWrapRk != null) mkWrapRk.wipe();
  }

  @Override
  public void close() {
    wipe();
  }
}
