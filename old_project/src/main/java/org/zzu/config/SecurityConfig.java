// src/main/java/org.zzu/config/SecurityConfig.java
package org.zzu.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.zzu.fliter.JwtAuthenticationFilter;
import org.zzu.utils.MD5Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@Slf4j
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter; //


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new PasswordEncoder() {
            @Override
            public String encode(CharSequence rawPassword) {
                return MD5Util.encrypt(rawPassword.toString());
            }

            @Override
            public boolean matches(CharSequence rawPassword, String encodedPassword) {
                return encodedPassword.equals(MD5Util.encrypt(rawPassword.toString()));
            }
        };
    }

    /**
     * 获取AuthenticationManager，以便在Controller中进行登录认证
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    /**
     * 配置Spring Security的过滤器链
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        log.info("✅ SecurityConfig is being loaded!"); // ← 加这行
        http
                // 1. 禁用csrf，因为我们使用JWT，不需要csrf保护
                .csrf(AbstractHttpConfigurer::disable)
                // 2. 设置Session管理策略为STATELESS（无状态），服务器不创建session
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // 3. 配置URL的授权规则
                .authorizeHttpRequests(auth -> auth
                        // 放行公共接口
                        .requestMatchers(
                                "/user/login",
                                "/user/register",
                                "/user/checkUsername/**",
                                "/user/captcha",
                                "/user/checkPing",
                                "/api/public/**",
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/static/**"
                        ).permitAll()
                        // 放行所有OPTIONS请求
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        // 管理员接口
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        // 需要认证的接口
                        .anyRequest().authenticated()
                )
                // 4. 将我们自定义的JWT过滤器添加到UsernamePasswordAuthenticationFilter之前
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}