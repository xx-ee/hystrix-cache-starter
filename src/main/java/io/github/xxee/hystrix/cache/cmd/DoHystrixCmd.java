package io.github.xxee.hystrix.cache.cmd;

import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.expression.ExpressionUtil;
import com.alibaba.fastjson.JSON;
import com.netflix.hystrix.*;
import io.github.xxee.hystrix.cache.util.SpUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import io.github.xxee.hystrix.cache.annotation.HystrixCmd;
import io.github.xxee.hystrix.cache.service.HystrixCacheService;

import java.lang.reflect.Method;
import java.util.Objects;

/**
 * Created by xiedong
 * Date: 2024/3/24 23:06
 */
@Slf4j
public class DoHystrixCmd extends HystrixCommand<Object> {
    private ProceedingJoinPoint jp;
    private Method method;
    private HystrixCmd hystrixCmd;
    private HystrixCacheService hystrixCacheService;
    private String cacheKey;

    public DoHystrixCmd(HystrixCmd hystrixCmd, HystrixCacheService hystrixCacheService) {
        /*********************************************************************************************
         * 置HystrixCommand的属性
         * GroupKey：            该命令属于哪一个组，可以帮助我们更好的组织命令。
         * CommandKey：          该命令的名称
         * ThreadPoolKey：       该命令所属线程池的名称，同样配置的命令会共享同一线程池，若不配置，会默认使用GroupKey作为线程池名称。
         * CommandProperties：   该命令的一些设置，包括断路器的配置，隔离策略，降级设置，以及一些监控指标等。
         * ThreadPoolProperties：关于线程池的配置，包括线程池大小，排队队列的大小等
         *********************************************************************************************/

        super(Setter.
                //设置groupKey
                        withGroupKey(HystrixCommandGroupKey.Factory.asKey(hystrixCmd.groupKey()))
                //设置commandKey
                .andCommandKey(HystrixCommandKey.Factory.asKey(hystrixCmd.commandKey()))
                //设置threadpoolKey
                .andThreadPoolKey(HystrixThreadPoolKey.Factory.asKey(hystrixCmd.threadPoolKey()))
                //命令配置
                .andCommandPropertiesDefaults(HystrixCommandProperties
                        .Setter()
                        //设置隔离策略为线程池隔离
                        .withExecutionIsolationStrategy(HystrixCommandProperties.ExecutionIsolationStrategy.THREAD)
                )
                //线程池参数配置
                .andThreadPoolPropertiesDefaults(
                        HystrixThreadPoolProperties
                                //设置核心数为10
                                .Setter()
                                .withCoreSize(10))
        );
        this.hystrixCmd = hystrixCmd;
        this.hystrixCacheService = hystrixCacheService;
    }

    public Object access(ProceedingJoinPoint jp, Method method, Object[] args) {
        this.jp = jp;
        this.method = method;
        this.cacheKey = generateCacheKey(method, args);
        return this.execute();
    }

    private String generateCacheKey(Method method, Object[] args) {
        try {
            if (this.hystrixCmd.enableCache() && StrUtil.isNotBlank(hystrixCmd.cacheKey())) {
                Object key = ExpressionUtil.eval(hystrixCmd.cacheKey(), SpUtil.getParamMap(method, args));
                return Objects.isNull(key) ? "" : this.hystrixCmd.cachePrefix() + key.toString();
            }
        } catch (Exception e) {
        }
        return "";
    }

    @Override
    protected Object run() throws Exception {
        try {
            if (this.hystrixCmd.enableCache() && this.hystrixCmd.useCacheFirst()) {
                Object result = this.hystrixCacheService.getFallbackData(this.cacheKey);
                if (result != null) {
                    log.info("hystrixCmd-run,use cache first,not call outer");
                    return JSON.parseObject(result.toString(), method.getReturnType());
                }
            }
            //
            log.info("hystrixCmd-run,start call outer");
            Object result = jp.proceed();

            if (this.hystrixCmd.enableCache()) {
                //记录fallbackData
                this.hystrixCacheService.putFallBackData(this.cacheKey, result);
            }
            return result;
        } catch (Throwable e) {
            log.error("hystrixCmd-error", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    protected Object getFallback() {
        log.info("hystrixCmd-getFallback...key:{},start", this.cacheKey);
        if (this.hystrixCmd.enableCache() && this.hystrixCmd.useCacheAfter()) {
            Object s = this.hystrixCacheService.getFallbackData(this.cacheKey);
            if (s != null) {
                log.info("hystrixCmd-getFallback...key:{},cache fallback enable and cache find,will return", this.cacheKey);
                return JSON.parseObject(s.toString(), method.getReturnType());
            }
        }
//        if (log.isDebugEnabled()) {
        log.info("hystrixCmd-getFallback...key:{},cache fallback not enable or cache expire,will return default", this.cacheKey);
//        }
        return JSON.parseObject(hystrixCmd.fallbackDefaultJson(), method.getReturnType());
    }
}


