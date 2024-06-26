package io.github.xxee.hystrix.cache.aspect;

import io.github.xxee.hystrix.cache.prometheus.PrometheusMetricReportor;
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
    private final PrometheusMetricReportor prometheusMetricReportor;

    public HystrixCmdAspect(HystrixCacheService hystrixCacheService, PrometheusMetricReportor prometheusMetricReportor) {
        this.hystrixCacheService = hystrixCacheService;
        this.prometheusMetricReportor = prometheusMetricReportor;
    }

    @Pointcut("@annotation(io.github.xxee.hystrix.cache.annotation.HystrixCmd)")
    public void aopPoint() {
    }

    @Around("aopPoint() && @annotation(doGovern)")
    public Object doRouter(ProceedingJoinPoint jp, HystrixCmd doGovern) throws Throwable {
        DoHystrixCmd doHystrixCommand = new DoHystrixCmd(doGovern, hystrixCacheService, prometheusMetricReportor);
        return doHystrixCommand.access(jp, getMethod(jp), jp.getArgs());
    }

    private Method getMethod(JoinPoint jp) throws NoSuchMethodException {
        Signature sig = jp.getSignature();
        MethodSignature methodSignature = (MethodSignature) sig;
        return jp.getTarget().getClass().getMethod(methodSignature.getName(), methodSignature.getParameterTypes());
    }
}
