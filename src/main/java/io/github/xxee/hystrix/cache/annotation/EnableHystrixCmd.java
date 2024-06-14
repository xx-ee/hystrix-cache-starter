package io.github.xxee.hystrix.cache.annotation;

import io.github.xxee.hystrix.cache.HystrixCmdAutoConfiguration;
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
@Import({HystrixCmdAutoConfiguration.class})
public @interface EnableHystrixCmd {

}
