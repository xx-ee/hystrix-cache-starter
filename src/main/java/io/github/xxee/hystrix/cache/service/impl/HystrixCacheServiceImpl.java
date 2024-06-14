package io.github.xxee.hystrix.cache.service.impl;

import com.alibaba.fastjson.JSON;
import com.alicp.jetcache.Cache;
import io.github.xxee.hystrix.cache.service.HystrixCacheService;

/**
 * Created by xiedong
 * Date: 2024/2/25 16:40
 */
public class HystrixCacheServiceImpl implements HystrixCacheService {
    private Cache<String, Object> hystrixCache;


    public HystrixCacheServiceImpl(Cache<String, Object> hystrixCache) {
        this.hystrixCache = hystrixCache;
    }

    @Override
    public Object getFallbackData(String key) {
        return hystrixCache.get(key);
    }

    @Override
    public void putFallBackData(String key, Object val) {
        hystrixCache.put(key, JSON.toJSONString(val));
    }
}
