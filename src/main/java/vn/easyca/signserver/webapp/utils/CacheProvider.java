package vn.easyca.signserver.webapp.utils;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import java.util.concurrent.TimeUnit;

public class CacheProvider {
    private static Cache<String, Object> cache;

    static {
        // Create a cache with a maximum size of 1000 and an expiration time of 10 minutes
        cache = CacheBuilder.newBuilder()
            .maximumSize(1000)
            .expireAfterWrite(10, TimeUnit.MINUTES)
            .build();
    }

    public static Cache<String, Object> getCache() {
        return cache;
    }

    public static void put(String key, String value) {
        cache.put(key, value);
    }
}
