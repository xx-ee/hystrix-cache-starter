package io.github.xxee.hystrix.cache.prometheus;

import cn.hutool.core.util.StrUtil;
import io.micrometer.core.instrument.Metrics;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by xiedong
 * 2024/6/26
 */
@Slf4j
public class PrometheusMetricReportor {
    public void fallBackSuccess(String group, String command) {
        this.fallBack(group, command, 1);
    }

    public void fallBackFail(String group, String command) {
        this.fallBack(group, command, 0);
    }

    public void cacheSuccess(String group, String command) {
        this.cache(group, command, 1);
    }

    public void cacheFail(String group, String command) {
        this.cache(group, command, 0);
    }


    public void cache(String group, String command, int status) {
        try {
            if (StrUtil.isAllBlank(group, command)) {
                return;
            }
            Metrics.counter("hystrix_cache_biz_counter",
                            "group", group,
                            "command", command,
                            "status", status + ""
                    )
                    .increment();
        } catch (Exception e) {
        }
    }

    public void fallBack(String group, String command, int status) {
        try {
            if (StrUtil.isAllBlank(group, command)) {
                return;
            }
            Metrics.counter("hystrix_fallback_biz_counter",
                            "group", group,
                            "command", command,
                            "status", status + ""
                    )
                    .increment();
        } catch (Exception e) {
        }
    }
}
