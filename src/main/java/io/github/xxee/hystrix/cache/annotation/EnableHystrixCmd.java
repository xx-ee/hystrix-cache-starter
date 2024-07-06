package io.github.xxee.hystrix.cache.annotation;

import com.alicp.jetcache.anno.config.CommonConfiguration;
import com.alicp.jetcache.anno.field.CreateCacheAnnotationBeanPostProcessor;
import io.github.xxee.hystrix.cache.HystrixCmdAutoConfiguration;
import org.springframework.cloud.netflix.hystrix.HystrixCircuitBreakerConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * Created by xiedong
 * 2024/6/14
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Import({HystrixCircuitBreakerConfiguration.class, HystrixCmdAutoConfiguration.class,})
//@Import({CommonConfiguration.class, CreateCacheAnnotationBeanPostProcessor.class})
public @interface EnableHystrixCmd {

}
