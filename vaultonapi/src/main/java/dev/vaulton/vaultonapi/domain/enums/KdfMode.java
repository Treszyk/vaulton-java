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

  public static KdfMode fromValue(int value) {
    for (KdfMode mode : values()) {
      if (mode.value == value) return mode;
    }
    throw new IllegalArgumentException("Unknown KdfMode value: " + value);
  }
}
