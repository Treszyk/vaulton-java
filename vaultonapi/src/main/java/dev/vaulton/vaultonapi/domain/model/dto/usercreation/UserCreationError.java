package dev.vaulton.vaultonapi.domain.model.dto.usercreation;

public enum UserCreationError {
  /** The client is using a crypto schema version the server doesn't support. */
  UNSUPPORTED_CRYPTO_SCHEMA,

  /** The Account ID already exists in the database. */
  ACCOUNT_EXISTS,

  /** The KDF mode provided is not supported or is invalid. */
  INVALID_KDF_MODE,

  /** One or more cryptographic blobs (Verifiers, Salts, etc.) are malformed. */
  INVALID_CRYPTO_BLOB
}
