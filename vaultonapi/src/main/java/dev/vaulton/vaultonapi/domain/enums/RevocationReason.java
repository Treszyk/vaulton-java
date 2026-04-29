package dev.vaulton.vaultonapi.domain.enums;

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
}
