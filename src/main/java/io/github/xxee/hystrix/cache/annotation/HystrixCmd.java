package io.github.xxee.hystrix.cache.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by xiedong
 * Date: 2024/3/24 22:57
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface HystrixCmd {
    String cachePrefix() default "default";

    String cacheKey() default "";
    boolean enableCache() default true;

    boolean useCacheFirst() default false;

    boolean useCacheAfter() default false;

    String fallbackDefaultJson() default "";

    /**
     * The command group key is used for grouping together commands such as for reporting,
     * alerting, dashboards or team/library ownership.
     *
     * @return group key
     */
    String groupKey() default "";

    /**
     * Hystrix command key.
     *
     * @return Hystrix command key.
     */
    String commandKey() default "";

    /**
     * The thread-pool key is used to represent a
     * HystrixThreadPool for monitoring, metrics publishing, caching and other such uses.
     *
     * @return thread-pool key
     */
    String threadPoolKey() default "";
}
