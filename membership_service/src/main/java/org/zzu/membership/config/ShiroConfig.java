package org.zzu.membership.config;

import jakarta.servlet.DispatcherType;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.DelegatingFilterProxy;
import org.zzu.membership.security.SimpleRealm;

import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.Map;

@Configuration
public class ShiroConfig {

    @Bean
    public Realm realm() {
        return new SimpleRealm();
    }

    @Bean
    public SecurityManager securityManager(Realm realm) {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager(realm);
        return securityManager;
    }

    @Bean(name = "shiroFilter")
    public ShiroFilterFactoryBean shiroFilterFactoryBean(SecurityManager securityManager) {
        ShiroFilterFactoryBean bean = new ShiroFilterFactoryBean();
        bean.setSecurityManager(securityManager);

        Map<String, String> chain = new LinkedHashMap<>();
        chain.put("/auth/login", "anon");
        chain.put("/auth/logout", "logout");
        chain.put("/membership/checkPing", "anon");
        chain.put("/membership/**", "authc");
        chain.put("/**", "anon");
        bean.setFilterChainDefinitionMap(chain);
        return bean;
    }

    
}

