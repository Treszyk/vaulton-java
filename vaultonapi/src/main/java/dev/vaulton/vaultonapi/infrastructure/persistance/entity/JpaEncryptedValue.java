package dev.vaulton.vaultonapi.infrastructure.persistance.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public record JpaEncryptedValue(
    @Column(nullable = false) byte[] nonce,
    @Column(nullable = false) byte[] cipherText,
    @Column(nullable = false) byte[] tag) {}
