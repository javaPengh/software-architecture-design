// 请确保包名与您的项目结构一致
package org.zzu.fliter;

import com.alibaba.druid.util.StringUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.zzu.mapper.UserMapper; // <-- 1. 添加 UserMapper 的 import
import org.zzu.pojo.User;
import org.zzu.utils.JwtHelper;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtHelper jwtHelper;

    // 移除了不再需要的 UserDetailsServiceImpl
    // @Autowired
    // private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private UserMapper userMapper; // <-- 2. 在这里注入 UserMapper

    public static final String REDIS_LOGIN_KEY_PREFIX = "login:uid:";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String token = request.getHeader("token");

        if (StringUtils.isEmpty(token)) {
            filterChain.doFilter(request, response);
            return;
        }

        Long userId;
        try {
            if (jwtHelper.isExpiration(token)) {
                filterChain.doFilter(request, response);
                return;
            }
            userId = jwtHelper.getUserId(token);
        } catch (Exception e) {
            e.printStackTrace();
            filterChain.doFilter(request, response);
            return;
        }

        String redisToken = (String) redisTemplate.opsForValue().get(REDIS_LOGIN_KEY_PREFIX + userId);
        if(StringUtils.isEmpty(redisToken) || !redisToken.equals(token)){
            filterChain.doFilter(request, response);
            return;
        }

        // 5. 获取用户信息 (已修正)
        // 直接使用 userMapper 通过 ID 查询用户，这是最直接高效的方式
        User user = userMapper.selectById(userId);

        // 6. 构建Authentication对象，并存入SecurityContextHolder
        // 确保从数据库中查到了用户
        if (user != null) {
            // 因为我们的 User 类已经实现了 UserDetails, 所以可以直接使用
            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        }

        filterChain.doFilter(request, response);
    }
}