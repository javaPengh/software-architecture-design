package org.zzu.config;

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