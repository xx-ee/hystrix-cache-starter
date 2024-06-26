package io.github.xxee.hystrix.cache.service.impl;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alicp.jetcache.Cache;
import com.alicp.jetcache.CacheInvokeException;
import io.github.xxee.hystrix.cache.service.HystrixCacheService;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import java.util.Objects;

/**
 * Created by xiedong
 * Date: 2024/2/25 16:40
 */
@Slf4j
public class HystrixCacheServiceImpl implements HystrixCacheService {
    private Cache<String, Object> hystrixCache;


    public HystrixCacheServiceImpl(Cache<String, Object> hystrixCache) {
        this.hystrixCache = hystrixCache;
    }

    @Override
    public Object getFallbackData(String key) {
        if (StrUtil.isBlank(key)) {
            return null;
        }
        try {
            return hystrixCache.get(key);
        } catch (Exception e) {
            log.error("getFallbackData-error,key:{},err", key, e);
            return null;
        }
    }

    @Override
    public void putFallBackData(String key, Object val) {
        if (StrUtil.isBlank(key)
                || Objects.isNull(val)
                || StrUtil.isBlank(val.toString())) {
            return;
        }
        try {
            hystrixCache.put(key, JSON.toJSONString(val));
        } catch (Exception e) {
            log.error("putFallBackData-error,key:{},err", key, e);
        }
    }
}
