package dev.vaulton.vaultonapi.domain.crypto;

import java.util.Arrays;

/**
 * A Value Object wrapping a byte array with controlled zeroization (wiping) capabilities.
 * Performs defensive cloning on both intake and output to prevent reference leakage
 * while allowing for safe memory clearing of sensitive cryptographic material.
 */
@SuppressWarnings("ClassCanBeRecord")
public final class SecureBuffer implements AutoCloseable {
    private final byte[] bytes;

    public SecureBuffer(byte[] bytes) {
        if (bytes == null) throw new IllegalArgumentException("Bytes cannot be null");
        this.bytes = bytes.clone();
    }

    public byte[] bytes() {
        return bytes.clone();
    }

    public int length() {
        return bytes.length;
    }

    public void wipe() {
        Arrays.fill(this.bytes, (byte) 0x00);
    }

    @Override
    public void close() {
        this.wipe();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SecureBuffer that = (SecureBuffer) o;
        return Arrays.equals(bytes, that.bytes);
    }

    @Override
    public int hashCode() {
        return java.util.Arrays.hashCode(bytes);
    }
}

