package xd.hystrix.cache.listener;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.context.ApplicationEvent;

import java.util.Map;

/**
 * Created by xiedong
 * 2024/2/26
 */
@Setter
@Getter
@ToString
public class HystrixPropertiesChangeEvent extends ApplicationEvent {
    private Map<String, Object> hystrixPropMap;

    public HystrixPropertiesChangeEvent(Object source, Map<String, Object> hystrixPropMap) {
        super(source);
        this.hystrixPropMap = hystrixPropMap;
    }
}
