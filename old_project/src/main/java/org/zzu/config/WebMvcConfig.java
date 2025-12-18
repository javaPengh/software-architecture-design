package org.zzu.config;

import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.zzu.fliter.LoginProtectInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Spring MVC 配置类
 * @className WebMvcConfig
 * @description 配置拦截器等Web组件
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private LoginProtectInterceptor loginProtectInterceptor;  // 登录保护拦截器

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                // 明确指定允许的来源，不要包含 "*"
                .allowedOriginPatterns("http://localhost:5173", "http://127.0.0.1:5173")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                // 允许携带凭证（Cookie/Token）
                .exposedHeaders("Captcha", "token", "SESSION_KEY")
                .allowCredentials(true)
                .maxAge(3600);
    }

    /**
     * 配置拦截器规则
     * @param registry 拦截器注册器
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 对电影相关路径启用登录保护
        registry.addInterceptor(loginProtectInterceptor).addPathPatterns("/movie/**");
        // 对影厅相关路径启用登录保护
        registry.addInterceptor(loginProtectInterceptor).addPathPatterns("/hall/**");
        // 对排片相关路径启用登录保护
        registry.addInterceptor(loginProtectInterceptor).addPathPatterns("/screening/**");
    }
}