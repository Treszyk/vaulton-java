package dev.vaulton.vaultonapi.domain.crypto;

/**
 * A Value Object wrapping a byte array to ensure immutability and value-based equality.
 * Performs defensive cloning on both intake and output to prevent reference leakage
 * and accidental mutation of sensitive cryptographic material.
 */
public record SecureBuffer(byte[] bytes) {
    public SecureBuffer {
        if (bytes == null) throw new IllegalArgumentException("Bytes cannot be null");
        bytes = bytes.clone();
    }

    @Override
    public byte[] bytes() {
        return bytes.clone();
    }

    public int length() {
        return bytes.length;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SecureBuffer that = (SecureBuffer) o;
        return java.util.Arrays.equals(bytes, that.bytes);
    }

    @Override
    public int hashCode() {
        return java.util.Arrays.hashCode(bytes);
    }

}

