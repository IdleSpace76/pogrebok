package ru.idles.utils;

import org.hashids.Hashids;

/**
 * Работа с хэшированнием строк
 *
 * @author a.zharov
 */
public class CryptoTool {

    private final Hashids hashids;

    public CryptoTool(String salt) {
        int minHashLength = 10;
        this.hashids = new Hashids(salt,  minHashLength);
    }

    /**
     * Получить хэш из Id
     */
    public String hashOf(Long value) {
        return hashids.encode(value);
    }

    /**
     * Получить Id из хэша
     */
    public Long idOf(String value) {
        long[] res = hashids.decode(value);
        if (res != null && res.length > 0) {
            return res[0];
        }
        return null;
    }
}
