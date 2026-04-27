package dev.vaulton.vaultonapi.domain.crypto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SecureBufferTest {

    @Test
    void shouldBeIndependentOfPassedArray() {
        byte[] initialBytes = new byte[] { 1, 2, 3, 4 };
        byte[] badBytesHandling = initialBytes;

        try (SecureBuffer secureBytesHandling = new SecureBuffer(initialBytes)) {
            initialBytes[0] = 3;
            assertFalse(java.util.Arrays.equals(initialBytes, secureBytesHandling.bytes()));
        }

        assertArrayEquals(initialBytes, badBytesHandling);
    }

    @Test
    void bytesMethodShouldReturnDefensiveCopy() {
        try (SecureBuffer secureBytesHandling = new SecureBuffer(new byte[] {1, 2, 3, 4})) {
            byte[] giveMeBytes = secureBytesHandling.bytes();
            giveMeBytes[0] = 127;

            assertArrayEquals(new byte[] {127, 2, 3, 4}, giveMeBytes);
            assertArrayEquals(new byte[] {1, 2, 3, 4}, secureBytesHandling.bytes());
        }
    }

    @Test
    void shouldReturnCorrectArrayLength() {
        try (SecureBuffer secureBuffer = new SecureBuffer(new byte[] {1, 2, 3, 4})) {
            assertEquals(4, secureBuffer.length());
        }
    }

    @Test
    void shouldZeroOutInternalArrayWhenWiped() {
        try (SecureBuffer secureBuffer = new SecureBuffer(new byte[] {1, 2, 3, 4})) {
            assertArrayEquals(new byte[] {1, 2, 3, 4}, secureBuffer.bytes());
            secureBuffer.wipe();
            assertArrayEquals(new byte[] {0, 0, 0, 0}, secureBuffer.bytes());
        }
    }

    @Test
    void shouldWipeAutomaticallyWhenClosed() {
        SecureBuffer ref;
        try (SecureBuffer buffer = new SecureBuffer(new byte[] {1, 2, 3, 4})) {
            ref = buffer;
            assertEquals(ref, buffer);
        }
        assertArrayEquals(new byte[] {0, 0, 0, 0}, ref.bytes());
    }

    @Test
    void shouldBeEqualWhenContentsMatch() {
        try (SecureBuffer buff1 = new SecureBuffer(new byte[] {1, 2, 3, 4})) {
            try (SecureBuffer buff2 = new SecureBuffer(new byte[] {1, 2, 3, 4})) {
                assertEquals(buff1, buff2);
            }
        }
    }

    @Test
    void shouldNotBeEqualWhenContentsDiffer() {
        try (SecureBuffer buff1 = new SecureBuffer(new byte[] {1, 2, 3, 4})) {
            try (SecureBuffer buff2 = new SecureBuffer(new byte[] {4, 3, 2, 1})) {
                assertNotEquals(buff1, buff2);
            }
        }
    }

    @Test
    void shouldHaveSameHashCodeWhenContentsMatch() {
        try (SecureBuffer buff1 = new SecureBuffer(new byte[] {1, 2, 3, 4})) {
            try (SecureBuffer buff2 = new SecureBuffer(new byte[] {1, 2, 3, 4})) {
                assertEquals(buff1.hashCode(), buff2.hashCode());
            }
        }
    }

    @Test
    void shouldHaveDifferentHashCodeWhenContentsDiffer() {
        try (SecureBuffer buff1 = new SecureBuffer(new byte[] {1, 2, 3, 4})) {
            try (SecureBuffer buff2 = new SecureBuffer(new byte[] {4, 3, 2, 1})) {
                assertNotEquals(buff1.hashCode(), buff2.hashCode());
            }
        }
    }
}