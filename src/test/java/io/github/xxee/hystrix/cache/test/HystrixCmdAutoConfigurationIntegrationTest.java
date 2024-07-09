package io.github.xxee.hystrix.cache.test;

import io.github.xxee.hystrix.cache.HystrixCmdAutoConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class HystrixCmdAutoConfigurationIntegrationTest {

    @Autowired
    private HystrixCmdAutoConfiguration hystrixCmdAutoConfiguration; // 替换为您实际的自动配置类

    @Test
    public void contextLoads() {
        assertNotNull(hystrixCmdAutoConfiguration); // 确保自动配置类被正确加载
        // 进行其他测试逻辑
    }

    @Configuration
    @Import(HystrixCmdAutoConfiguration.class) // 替换为实际的自动配置类
    static class TestConfig {
        // 可以在这里提供任何测试需要的额外配置
    }
}