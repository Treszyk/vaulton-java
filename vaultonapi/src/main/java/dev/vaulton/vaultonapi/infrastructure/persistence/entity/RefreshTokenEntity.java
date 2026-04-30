package dev.vaulton.vaultonapi.infrastructure.persistence.entity;

import dev.vaulton.vaultonapi.domain.enums.RevocationReason;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "RefreshTokens")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RefreshTokenEntity {
  @Id
  @Column(name = "Id")
  private UUID id;

  @Column(name = "UserId", nullable = false)
  private UUID userId;

  @Column(name = "TokenHash", nullable = false)
  private byte[] tokenHash;

  @Column(name = "AccessTokenJtiHash")
  private byte[] accessTokenJtiHash;

  @Column(name = "CreatedAt", nullable = false)
  private Instant createdAt;

  @Column(name = "ExpiresAt", nullable = false)
  private Instant expiresAt;

  @Column(name = "RevokedAt")
  private Instant revokedAt;

  @Column(name = "RevocationReason")
  private RevocationReason revocationReason;
}
