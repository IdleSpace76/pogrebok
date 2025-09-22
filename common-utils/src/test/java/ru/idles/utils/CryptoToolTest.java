package ru.idles.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author a.zharov
 */
public class CryptoToolTest {

    private final CryptoTool tool = new CryptoTool("test-salt");

    @Test
    void hashIsDeterministic() {
        String h1 = tool.hashOf(123L);
        String h2 = tool.hashOf(123L);
        Assertions.assertEquals(h1, h2);
    }

    @Test
    void differentSaltsProduceDifferentHashes() {
        CryptoTool other = new CryptoTool("other-salt");
        Assertions.assertNotEquals(tool.hashOf(123L), other.hashOf(123L));
    }

    @Test
    void hashLooksReasonable() {
        String h = tool.hashOf(1L);
        Assertions.assertNotNull(h);
        Assertions.assertFalse(h.isBlank());
    }
}
