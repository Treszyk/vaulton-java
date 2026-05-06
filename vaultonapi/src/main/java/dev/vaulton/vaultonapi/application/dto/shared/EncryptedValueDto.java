package dev.vaulton.vaultonapi.application.dto.shared;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Arrays;

public record EncryptedValueDto(
    @JsonProperty("Nonce") byte[] nonce,
    @JsonProperty("CipherText") byte[] cipherText,
    @JsonProperty("Tag") byte[] tag)
    implements AutoCloseable {
  public void wipe() {
    if (nonce != null) Arrays.fill(nonce, (byte) 0);
    if (cipherText != null) Arrays.fill(cipherText, (byte) 0);
    if (tag != null) Arrays.fill(tag, (byte) 0);
  }

  @Override
  public void close() {
    wipe();
  }
}
