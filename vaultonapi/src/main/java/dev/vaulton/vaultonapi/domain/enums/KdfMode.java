package dev.vaulton.vaultonapi.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Defines the security level for Key Derivation, mapping to the intensity of the Argon2id
 * parameters on the frontend.
 */
@Getter
@AllArgsConstructor
public enum KdfMode {
  DEFAULT(1),
  STRONG(2);

  private final int value;
}
