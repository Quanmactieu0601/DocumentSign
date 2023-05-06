package pki.sign.cache;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import java.util.concurrent.TimeUnit;

/**
 * Created by chen on 7/26/17.
 */
public class GuavaCache {

    private static GuavaCache instance = null;

    private final long MAX_RECORDS = 10000;
    private final int TIME_OUT_IN_SECONDS = 60;
    private final int DEFAULT_CONCURRENCY_LEVEL = 4;
    private Cache<Object, AbstractCachedObject> cache = null;

    public static GuavaCache getInstance() {
        if (instance == null) {
            instance = new GuavaCache();
        }
        return instance;
    }

    private GuavaCache() {
        cache =
            CacheBuilder
                .newBuilder()
                .maximumSize(MAX_RECORDS)
                .expireAfterWrite(TIME_OUT_IN_SECONDS, TimeUnit.SECONDS)
                .concurrencyLevel(DEFAULT_CONCURRENCY_LEVEL)
                .recordStats()
                .build();
    }

    public void set(Object key, AbstractCachedObject value) {
        cache.put(key, value);
    }

    public AbstractCachedObject get(Object key) {
        return cache.getIfPresent(key);
    }

    public boolean contain(Object key) {
        return cache.getIfPresent(key) != null;
    }

    public void remove(Object key) {
        if (contain(key)) cache.invalidate(key);
    }

    public boolean isFull() {
        return cache.size() == MAX_RECORDS;
    }
}
