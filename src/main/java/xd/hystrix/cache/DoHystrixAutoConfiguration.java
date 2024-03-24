package xd.hystrix.cache;

import com.alicp.jetcache.Cache;
import com.alicp.jetcache.CacheManager;
import com.alicp.jetcache.anno.CacheType;
import com.alicp.jetcache.template.QuickConfig;
import com.netflix.hystrix.Hystrix;
import com.soundcloud.prometheus.hystrix.HystrixPrometheusMetricsPublisher;
import io.micrometer.prometheus.PrometheusMeterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import xd.hystrix.cache.annotation.HystrixCmd;
import xd.hystrix.cache.aspect.HystrixCmdAspect;
import xd.hystrix.cache.listener.HystrixPropertiesListener;
import xd.hystrix.cache.service.HystrixCacheService;
import xd.hystrix.cache.service.impl.HystrixCacheServiceImpl;

import java.time.Duration;

/**
 * Created by xiedong
 * 2024/2/26
 */
@Configuration
@ConditionalOnClass(value = {
        HystrixCmd.class
})
@ConditionalOnBean(CacheManager.class)
@Slf4j
public class DoHystrixAutoConfiguration {
    public static final String HystrixCache = "hystrixCache";

    @Bean
    @ConditionalOnMissingBean
    public HystrixCacheService hystrixCacheService(@Autowired @Qualifier(value = HystrixCache)
                                                   Cache<String, Object> hystrixCache) {
        return new HystrixCacheServiceImpl(hystrixCache);
    }

    @Bean(name = HystrixCache, destroyMethod = "close")
    @ConditionalOnMissingBean
    public Cache<String, Object> getHystrixCache(@Autowired CacheManager cacheManager) {
        QuickConfig qc = QuickConfig.newBuilder(HystrixCache)
                .expire(Duration.ofSeconds(5))
                .cacheType(CacheType.LOCAL) // two level cache
                .localLimit(50)
                .syncLocal(true) // invalidate local cache in all jvm process after update
                .build();
        return cacheManager.getOrCreateCache(qc);
    }


    @Bean
    public HystrixCmdAspect doHystrixAspect(@Autowired HystrixCacheService hystrixCacheService) {
        return new HystrixCmdAspect(hystrixCacheService);
    }

    @Bean
    public HystrixShutdownHook hystrixShutdownHook() {
        return new HystrixShutdownHook();
    }

    private class HystrixShutdownHook implements DisposableBean {
        private HystrixShutdownHook() {
        }

        public void destroy() throws Exception {
            Hystrix.reset();
        }
    }

    @Bean
    @ConditionalOnClass(value = {
            PrometheusMeterRegistry.class,
            HystrixCmd.class
    })
    public HystrixPrometheus hystrixPrometheus(@Autowired PrometheusMeterRegistry prometheusMeterRegistry) {
        return new HystrixPrometheus(prometheusMeterRegistry);
    }


    private class HystrixPrometheus {
        private HystrixPrometheus(PrometheusMeterRegistry prometheusMeterRegistry) {
            HystrixPrometheusMetricsPublisher.builder()
                    .shouldExportProperties(true)
                    .shouldExportDeprecatedMetrics(true)
                    .withRegistry(prometheusMeterRegistry.getPrometheusRegistry()).buildAndRegister();
            log.info("hystrix prometheus adaptored...");
        }
    }

    @Bean
    public HystrixPropertiesListener hystrixPropertiesListener() {
        return new HystrixPropertiesListener();
    }

}
