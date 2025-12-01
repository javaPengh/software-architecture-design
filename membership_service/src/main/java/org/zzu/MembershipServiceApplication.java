package org.zzu;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableDiscoveryClient  // 启用服务注册与发现
@EnableFeignClients(basePackages = "org.zzu.membership")
@RefreshScope  // 启用配置刷新
public class MembershipServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(MembershipServiceApplication.class, args);
    }
}
