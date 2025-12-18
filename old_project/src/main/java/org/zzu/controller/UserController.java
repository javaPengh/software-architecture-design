package org.zzu.controller;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.LineCaptcha;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.servlet.http.HttpServletRequest; // 合并：需要引入 request 来获取验证码
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.zzu.fliter.JwtAuthenticationFilter;
import org.zzu.pojo.ChangePasswordDto;
import org.zzu.pojo.LoginDto;
import org.zzu.pojo.User;
import org.zzu.service.UserService;
import org.zzu.utils.*;
import lombok.extern.slf4j.Slf4j;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("user")
@Slf4j
public class UserController {
    private final static String SESSION_KEY = "Captcha";

    @Autowired
    private UserService userService;
    @Autowired
    private JwtHelper jwtHelper;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private MeterRegistry meterRegistry;


    @PostMapping("login")
    public Result login(@RequestBody LoginDto loginDto, HttpServletRequest request) { // 合并：添加 HttpServletRequest
        // 合并点 1：从 GitHub 版本中加入验证码校验逻辑
        String realCaptcha = request.getHeader(SESSION_KEY);
        String captcha = loginDto.getCaptcha();
        // 注意：你需要确保 loginDto 中有 captcha 字段
        if (captcha == null || realCaptcha == null || !captcha.equalsIgnoreCase(realCaptcha)) {
            return Result.build(null, ResultCodeEnum.CAPTCHA_ERROR);
        }

        // 保留你的 Spring Security 认证逻辑
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(loginDto.getUsername(), loginDto.getPassword());
        Authentication authenticate;
        try {
            authenticate = authenticationManager.authenticate(authenticationToken);
        } catch (Exception e) {
            // 登录失败，可能是用户名或密码错误
            return Result.build(null, ResultCodeEnum.USERNAME_ERROR); // 或 PASSWORD_ERROR
        }

        User loginUser = (User) authenticate.getPrincipal();

        String token = jwtHelper.createToken(Long.valueOf(loginUser.getUid()));

        redisTemplate.opsForValue().set(
                JwtAuthenticationFilter.REDIS_LOGIN_KEY_PREFIX + loginUser.getUid(),
                token,
                jwtHelper.getTokenExpiration(),
                TimeUnit.MINUTES
        );

        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("role", loginUser.getType());
        data.put("nickname", loginUser.getNickname());

        meterRegistry.counter("user.login.success").increment();
        return Result.ok(data);
    }


    @PostMapping("logout")
    public Result logout() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof User)) {
            return Result.build(null, ResultCodeEnum.NOTLOGIN);
        }
        User loginUser = (User) authentication.getPrincipal();
        redisTemplate.delete(JwtAuthenticationFilter.REDIS_LOGIN_KEY_PREFIX + loginUser.getUid());
        return Result.ok(null);
    }


    @GetMapping("getUserInfo")
    public Result userInfo() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof User)) {
            return Result.build(null, ResultCodeEnum.NOTLOGIN);
        }
        User loginUser = (User) authentication.getPrincipal();
        loginUser.setPassword(null); // 安全起见，不返回密码
        Map<String, Object> data = new HashMap<>();
        data.put("loginUser", loginUser);
        return Result.ok(data);
    }

    @PostMapping("checkUsername/{username}")
    public Result checkUserName(@PathVariable String username) {
        return userService.checkUserName(username);
    }

    @PostMapping("register")
    public Result register(@RequestBody User user) {
        return userService.register(user);
    }


    @PostMapping("changePassword")
    public Result changePassword(@RequestBody ChangePasswordDto data) {
        User user = userService.getById(data.getUid());
        if (user == null) {
            return Result.build(null, ResultCodeEnum.SYSTEM_ERROR); // 用户不存在
        }
        // 注意：这里仍然使用 MD5Util。在 Spring Security 体系下，推荐使用 PasswordEncoder
        if (!user.getPassword().equals(MD5Util.encrypt(data.getOldPwd()))) {
            return Result.build(null, ResultCodeEnum.PASSWORD_ERROR);
        }
        user.setPassword(MD5Util.encrypt(data.getNewPwd()));
        userService.updateById(user);
        return Result.ok(null);
    }


    @GetMapping("captcha")
    public void generateCaptcha(HttpServletResponse response) throws IOException {
        log.info("✅ 收到验证码请求，Session ID");
        // 1. 设置响应头为图片类型
        response.setContentType("image/png");
        LineCaptcha captcha = CaptchaUtil.createLineCaptcha(100, 30, 4, 40);
        // 注意：将验证码存储到 Redis 会是更好的实践，但这里为了保持简单，还是用 Header
        response.addHeader(SESSION_KEY, captcha.getCode());
        captcha.write(response.getOutputStream());
    }

    // 合并点 2：GitHub 版本中的 checkLogin 方法被删除，因为新架构下由 JWT 过滤器实现

    @GetMapping("checkPing")
    public Result checkPing() {
        return Result.ok("请求通的");
    }
}