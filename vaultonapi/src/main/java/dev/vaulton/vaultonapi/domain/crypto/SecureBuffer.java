package dev.vaulton.vaultonapi.domain.crypto;

import lombok.Getter;

import java.util.Arrays;

/**
 * A Value Object wrapping a byte array with controlled zeroization (wiping) capabilities.
 * Performs defensive cloning on both intake and output to prevent reference leakage
 * while allowing for safe memory clearing of sensitive cryptographic material.
 */
@SuppressWarnings("ClassCanBeRecord")
public final class SecureBuffer implements AutoCloseable {
    private final byte[] bytes;
    private final int cachedHashCode;
    @Getter
    private boolean wiped = false;

    public SecureBuffer(byte[] bytes) {
        if (bytes == null) throw new IllegalArgumentException("Bytes cannot be null");
        this.bytes = bytes.clone();
        this.cachedHashCode = Arrays.hashCode(this.bytes);
    }

    public byte[] bytes() {
        if (wiped) throw new IllegalStateException("Cannot use a wiped SecureBuffer");
        return bytes.clone();
    }

    public int length() {
        if (wiped) throw new IllegalStateException("Cannot use a wiped SecureBuffer");
        return bytes.length;
    }

    public void wipe() {
        Arrays.fill(this.bytes, (byte) 0x00);
        wiped = true;
    }

    @Override
    public void close() {
        this.wipe();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o instanceof SecureBuffer that) {
            if (this.wiped || that.wiped) return false;
            return Arrays.equals(this.bytes, that.bytes);
        }
        return false;
    }


    @Override
    public int hashCode() {
        return cachedHashCode;
    }
}

