package dev.vaulton.vaultonapi.infrastructure.persistence.entity;

import dev.vaulton.vaultonapi.domain.enums.KdfMode;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;
import lombok.*;

@Entity
@Table(name = "Users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserEntity {
  @Id
  @Column(name = "Id")
  private UUID id;

  @Column(name = "Verifier", nullable = false)
  private byte[] verifier;

  @Column(name = "S_Verifier", nullable = false)
  private byte[] saltVerifier;

  @Column(name = "AdminVerifier", nullable = false)
  private byte[] adminVerifier;

  @Column(name = "S_AdminVerifier", nullable = false)
  private byte[] saltAdminVerifier;

  @Column(name = "S_Pwd", nullable = false)
  private byte[] saltPwd;

  @Column(name = "KdfMode", nullable = false)
  private KdfMode kdfMode;

  @Embedded
  @AttributeOverrides({
    @AttributeOverride(
        name = "nonce",
        column = @Column(name = "MkWrapPwd_Nonce", nullable = false)),
    @AttributeOverride(
        name = "cipherText",
        column = @Column(name = "MkWrapPwd_CipherText", nullable = false)),
    @AttributeOverride(name = "tag", column = @Column(name = "MkWrapPwd_Tag", nullable = false))
  })
  private JpaEncryptedValue mkWrapPwd;

  @Embedded
  @AttributeOverrides({
    @AttributeOverride(name = "nonce", column = @Column(name = "MkWrapRk_Nonce", nullable = false)),
    @AttributeOverride(
        name = "cipherText",
        column = @Column(name = "MkWrapRk_CipherText", nullable = false)),
    @AttributeOverride(name = "tag", column = @Column(name = "MkWrapRk_Tag", nullable = false))
  })
  private JpaEncryptedValue mkWrapRk;

  @Column(name = "RkVerifier")
  private byte[] rkVerifier;

  @Column(name = "S_Rk")
  private byte[] saltRk;

  @Column(name = "CryptoSchemaVer", nullable = false)
  private Integer cryptoSchemaVer;

  @Column(name = "CreatedAt", nullable = false)
  private Instant createdAt;

  @Column(name = "UpdatedAt", nullable = false)
  private Instant updatedAt;

  @Column(name = "LastLoginAt")
  private Instant lastLoginAt;

  @Column(name = "FailedLoginCount", nullable = false)
  private Integer failedLoginCount;

  @Column(name = "LastFailedLoginAt")
  private Instant lastFailedLoginAt;

  @Column(name = "LockedUntil")
  private Instant lockedUntil;
}
