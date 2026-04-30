package dev.vaulton.vaultonapi.infrastructure.persistence.repository;

import dev.vaulton.vaultonapi.domain.enums.RevocationReason;
import dev.vaulton.vaultonapi.infrastructure.persistence.entity.RefreshTokenEntity;
import jakarta.transaction.Transactional;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RefreshTokenJpaRepository extends JpaRepository<RefreshTokenEntity, UUID> {
  Optional<RefreshTokenEntity> findByTokenHash(byte[] tokenHash);

  List<RefreshTokenEntity> findAllByUserId(UUID userId);

  @Transactional
  @Modifying(clearAutomatically = true, flushAutomatically = true)
  @Query(
      "UPDATE RefreshTokenEntity r SET r.revokedAt = :revokedAt, r.revocationReason = :reason "
          + "WHERE r.userId = :userId AND r.revokedAt IS NULL")
  int revokeAllByUserId(
      @Param("userId") UUID userId,
      @Param("revokedAt") Instant revokedAt,
      @Param("reason") RevocationReason reason);
}
