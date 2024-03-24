package xd.hystrix.cache.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;

import java.util.Map;
import java.util.Objects;

/**
 * Created by xiedong
 * 2024/2/26
 */
@Slf4j
public class HystrixPropertiesListener implements ApplicationListener<HystrixPropertiesChangeEvent> {
    @Override
    public void onApplicationEvent(HystrixPropertiesChangeEvent event) {
        log.info("HystrixPropertiesListener-accept:{}", event);
        Map<String, Object> hystrixPropMap = event.getHystrixPropMap();

        for (Map.Entry<String, Object> entry : hystrixPropMap.entrySet()) {
            Object value = entry.getValue();
            String key = entry.getKey();
            System.setProperty(key, Objects.toString(value));
        }
    }
}
