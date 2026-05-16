package dev.vaulton.vaultonapi.domain.enums;

import dev.vaulton.vaultonapi.domain.exception.VaultonDomainException;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Specifies the reason why a Refresh Token and its associated session were invalidated or revoked.
 */
@Getter
@AllArgsConstructor
public enum RevocationReason {
  REGULAR(0),
  SECURITY(1);

  private final int value;

  public static RevocationReason fromValue(int value) {
    for (RevocationReason reason : values()) {
      if (reason.value == value) return reason;
    }
    throw new VaultonDomainException(
        "Unknown RevocationReason value: " + value, "Invalid revocation reason.");
  }
}
