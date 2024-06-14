package io.github.xxee.hystrix.cache.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import io.github.xxee.hystrix.cache.annotation.HystrixCmd;
import io.github.xxee.hystrix.cache.cmd.DoHystrixCmd;
import io.github.xxee.hystrix.cache.service.HystrixCacheService;

import java.lang.reflect.Method;

/**
 * Created by xiedong
 * Date: 2024/3/24 22:59
 */
@Aspect
public class HystrixCmdAspect {
    private final HystrixCacheService hystrixCacheService;

    public HystrixCmdAspect(HystrixCacheService hystrixCacheService) {
        this.hystrixCacheService = hystrixCacheService;
    }

    @Pointcut("@annotation(io.github.xxee.hystrix.cache.annotation.HystrixCmd)")
    public void aopPoint() {
    }

    @Around("aopPoint() && @annotation(doGovern)")
    public Object doRouter(ProceedingJoinPoint jp, HystrixCmd doGovern) throws Throwable {
        DoHystrixCmd doHystrixCommand = new DoHystrixCmd(doGovern, hystrixCacheService);
        return doHystrixCommand.access(jp, getMethod(jp), jp.getArgs());
    }

    private Method getMethod(JoinPoint jp) throws NoSuchMethodException {
        Signature sig = jp.getSignature();
        MethodSignature methodSignature = (MethodSignature) sig;
        return jp.getTarget().getClass().getMethod(methodSignature.getName(), methodSignature.getParameterTypes());
    }
}
