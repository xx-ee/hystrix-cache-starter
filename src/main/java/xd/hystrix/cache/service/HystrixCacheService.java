package xd.hystrix.cache.service;

/**
 * Created by xiedong
 * Date: 2024/2/25 16:27
 */
public interface HystrixCacheService {
    Object getFallbackData(String key);

    void putFallBackData(String key, Object val);
}
