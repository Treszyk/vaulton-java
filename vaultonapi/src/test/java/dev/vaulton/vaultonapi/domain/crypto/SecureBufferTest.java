package dev.vaulton.vaultonapi.domain.crypto;

import org.junit.jupiter.api.Test;

import java.util.HashMap;

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
            assertThrows(IllegalStateException.class, secureBuffer::bytes);
        }
    }

    @Test
    void shouldWipeAutomaticallyWhenClosed() {
        SecureBuffer ref;
        try (SecureBuffer buffer = new SecureBuffer(new byte[] {1, 2, 3, 4})) {
            ref = buffer;
            assertEquals(ref, buffer);
        }
        assertThrows(IllegalStateException.class, ref::bytes);
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

    @Test
    void shouldThrowWhenAccessingWipedBuffer() {
        try (var buffer = new SecureBuffer(new byte[]{1, 2})) {
            buffer.wipe();

            assertThrows(IllegalStateException.class, buffer::bytes);
            assertThrows(IllegalStateException.class, buffer::length);
            
            assertEquals(buffer, buffer); 
            assertNotEquals(buffer, new SecureBuffer(new byte[]{1, 2}));
        }
    }

    @Test
    void shouldKeepHashCodeStableAfterWipe() {
        try (SecureBuffer buffer = new SecureBuffer(new byte[]{9, 9, 9})) {
            int beforeWipe = buffer.hashCode();

            buffer.wipe();

            assertEquals(beforeWipe, buffer.hashCode());
        }
    }

    @Test
    void shouldRemainAddressableInHashMapAfterWipe() {
        SecureBuffer buffer = new SecureBuffer(new byte[] { 7, 7, 7 });
        HashMap<SecureBuffer, String> map = new HashMap<>();
        map.put(buffer, "value");

        buffer.wipe();
        assertEquals("value", map.get(buffer));
        assertEquals("value", map.remove(buffer));
        assertNull(map.get(new SecureBuffer(new byte[]{7, 7, 7})));
        assertTrue(map.isEmpty());
    }

}