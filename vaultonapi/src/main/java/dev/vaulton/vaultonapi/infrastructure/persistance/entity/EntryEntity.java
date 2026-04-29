package dev.vaulton.vaultonapi.infrastructure.persistance.entity;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "Entries")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EntryEntity {
  @Id private UUID id;

  @Column(name = "UserId", nullable = false)
  private UUID userId;

  @Embedded
  @AttributeOverrides({
    @AttributeOverride(name = "nonce", column = @Column(name = "Payload_Nonce", nullable = false)),
    @AttributeOverride(
        name = "cipherText",
        column = @Column(name = "Payload_CipherText", nullable = false)),
    @AttributeOverride(name = "tag", column = @Column(name = "Payload_Tag", nullable = false))
  })
  private JpaEncryptedValue payload;

  @Column(name = "CreatedAt", nullable = false)
  private Instant createdAt;

  @Column(name = "UpdatedAt", nullable = false)
  private Instant updatedAt;
}
