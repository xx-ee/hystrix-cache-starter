package io.github.xxee.hystrix.cache.refresh;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.ctrip.framework.apollo.Config;
import com.ctrip.framework.apollo.ConfigChangeListener;
import com.ctrip.framework.apollo.ConfigService;
import com.ctrip.framework.apollo.model.ConfigChange;
import com.ctrip.framework.apollo.spring.annotation.EnableApolloConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by xiedong
 * 2024/07/09
 */
@Configuration
@EnableApolloConfig
@ConditionalOnClass(name = "com.ctrip.framework.foundation.internals.provider.DefaultApplicationProvider")
@Slf4j
public class HystrixApolloAutoConfiguration {

    @Value("${apollo.bootstrap.namespaces:}")
    private String apolloNamespace;

    @Bean
    @ConditionalOnMissingBean
    public HystrixConfig hystrixConfig() {
        try {
            List<String> nsList = StrUtil.split(apolloNamespace, ',').stream().filter(StrUtil::isNotBlank).collect(Collectors.toList());
            if (CollUtil.isEmpty(nsList)) {
                return new HystrixConfig();
            }
            for (String ns : nsList) {
                Config config = ConfigService.getConfig(ns);
                ConfigChangeListener defaultChangeListener = changeEvent -> {
                    log.warn("Changes for namespace {}", changeEvent.getNamespace());
                    for (String key : changeEvent.changedKeys()) {
                        ConfigChange change = changeEvent.getChange(key);
                        if (change != null) {
                            log.warn("Change - key: {}, oldValue: {}, newValue: {}, changeType: {}",
                                    change.getPropertyName(), change.getOldValue(), change.getNewValue(),
                                    change.getChangeType());
                        }
                    }
                };

                ConfigChangeListener hystrixListener = changeEvent -> {
                    log.warn("Hystrix-Changes for namespace {}", changeEvent.getNamespace());
                    for (String key : changeEvent.changedKeys()) {
                        ConfigChange change = changeEvent.getChange(key);
                        if (change != null && StrUtil.startWith(key, "hystrix.")) {
                            log.warn("Hystrix-Change - key: {}, oldValue: {}, newValue: {}, changeType: {}",
                                    change.getPropertyName(), change.getOldValue(), change.getNewValue(),
                                    change.getChangeType());

                            System.setProperty(change.getPropertyName(), change.getNewValue());
                        }
                    }
                };
                config.addChangeListener(defaultChangeListener);
                config.addChangeListener(hystrixListener);
            }
        } catch (Exception e) {
            log.error("hystrixConfig-error", e);
        }
        return new HystrixConfig();
    }
}