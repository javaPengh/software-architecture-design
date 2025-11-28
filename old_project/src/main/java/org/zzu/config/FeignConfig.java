package org.zzu.config;

import feign.Logger;
import feign.Request;
import feign.Retryer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
public class FeignConfig {
    
    /**
     * Configure Feign logging level
     */
    @Bean
    public Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL;
    }
    
    /**
     * Configure Feign retry mechanism
     */
    @Bean
    public Retryer feignRetryer() {
        // Max attempts 3, initial interval 100ms, max interval 1s
        return new Retryer.Default(100, TimeUnit.SECONDS.toMillis(1), 3);
    }
    
    /**
     * Configure request options
     */
    @Bean
    public Request.Options options() {
        // Connect timeout 10s, read timeout 60s
        return new Request.Options(10, TimeUnit.SECONDS, 60, TimeUnit.SECONDS, true);
    }
}
