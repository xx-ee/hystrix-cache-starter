package io.github.xxee.hystrix.cache.annotation;

import io.github.xxee.hystrix.cache.HystrixCmdAutoConfiguration;
import io.github.xxee.hystrix.cache.refresh.HystrixApolloAutoConfiguration;
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
@Import({HystrixCircuitBreakerConfiguration.class, HystrixCmdAutoConfiguration.class, HystrixApolloAutoConfiguration.class})
//@Import({CommonConfiguration.class, CreateCacheAnnotationBeanPostProcessor.class})
public @interface EnableHystrixCmd {

}
