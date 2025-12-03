package org.zzu.controller;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.LineCaptcha;
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

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("user")
@CrossOrigin
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

    @PostMapping("login")
    public Result login(@RequestBody LoginDto loginDto) {
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(loginDto.getUsername(), loginDto.getPassword());
        Authentication authenticate;
        try {
            authenticate = authenticationManager.authenticate(authenticationToken);
        } catch (Exception e) {
            return Result.build(null, ResultCodeEnum.USERNAME_ERROR);
        }

        User loginUser = (User) authenticate.getPrincipal();

        String token = jwtHelper.createToken(Long.valueOf(loginUser.getUid()));

        redisTemplate.opsForValue().set(
                JwtAuthenticationFilter.REDIS_LOGIN_KEY_PREFIX + loginUser.getUid(),
                token,
                jwtHelper.getTokenExpiration(),
                TimeUnit.MINUTES
        );

        // ↓↓↓↓ 关键修改: 构建前端需要的数据结构 ↓↓↓↓
        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        // 使用 "role" 作为键名，值为数据库中的 type 字段
        data.put("role", loginUser.getType());
        data.put("nickname", loginUser.getNickname());

        return Result.ok(data);
    }

    // ... 其他方法 (logout, getUserInfo, register 等) 保持不变 ...

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
        loginUser.setPassword(null);
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
            return Result.build(null, ResultCodeEnum.SYSTEM_ERROR);
        }
        if (!user.getPassword().equals(MD5Util.encrypt(data.getOldPwd()))) {
            return Result.build(null, ResultCodeEnum.PASSWORD_ERROR);
        }
        user.setPassword(MD5Util.encrypt(data.getNewPwd()));
        userService.updateById(user);
        return Result.ok(null);
    }

    @GetMapping("captcha")
    @CrossOrigin(origins = "*", exposedHeaders = SESSION_KEY)
    public void generateCaptcha(HttpServletResponse response) throws IOException {
        LineCaptcha captcha = CaptchaUtil.createLineCaptcha(100, 30, 4, 40);
        response.addHeader(SESSION_KEY, captcha.getCode());
        captcha.write(response.getOutputStream());
    }
}