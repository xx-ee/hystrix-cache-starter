package xd.hystrix.cache.util;

//import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by xiedong
 * Date: 2024/2/25 18:06
 */
public class SpUtil {
    public static Map<String, Object> getParamMap(Method method, Object args[]) {

        ParameterNameDiscoverer parameterNameDiscoverer = new DefaultParameterNameDiscoverer();
        String[] parameterNames = parameterNameDiscoverer.getParameterNames(method);

        // 创建参数Map
        Map<String, Object> paramMap = new HashMap<>();
        if (parameterNames != null && parameterNames.length == args.length) {
            for (int i = 0; i < args.length; i++) {
                paramMap.put(parameterNames[i], args[i]);
            }
        }

        return paramMap;
    }
}
