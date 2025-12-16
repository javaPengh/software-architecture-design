package org.zzu.controller;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.LineCaptcha;
import com.alibaba.druid.util.StringUtils;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.zzu.dto.ChangePasswordDto;
import org.zzu.dto.LoginDto;
import org.zzu.pojo.User;
import org.zzu.service.UserService;
import org.zzu.utils.JwtHelper;
import org.zzu.utils.MD5Util;
import org.zzu.utils.Result;
import org.zzu.utils.ResultCodeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

/**
 * @className UserController
 * @description 对用户表进行增删改查
 */
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
    private MeterRegistry meterRegistry;
    @PostMapping("login")
    public Result login(@RequestBody LoginDto loginDto, HttpServletRequest request) {
        String realCaptcha = request.getHeader(SESSION_KEY);
        String captcha = loginDto.getCaptcha();
        if (!captcha.equalsIgnoreCase(realCaptcha)) {
            return Result.build(null, ResultCodeEnum.CAPTCHA_ERROR);
        }
        // 调用userService.login方法
        Result result = userService.login(loginDto);

        // 如果登录成功，记录登录次数
        if (result.getCode() == 200) {
            meterRegistry.counter("user.login.success").increment();
        }

        return userService.login(loginDto);
    }


    @GetMapping("getUserInfo")
    public Result userInfo(@RequestHeader String token) {
        return userService.getUserInfo(token);
    }


    @PostMapping("checkUsername/{username}")
    public Result checkUserName(@PathVariable String username) {
        return userService.checkUserName(username);
    }

    @PostMapping("register")
    public Result register(@RequestBody User user) {
        return userService.register(user);
    }

    @GetMapping("checkLogin")
    public Result checkLogin(@RequestHeader String token) {
        if (StringUtils.isEmpty(token) || jwtHelper.isExpiration(token)) {
            //没有传或者过期 未登录
            return Result.build(null, ResultCodeEnum.NOTLOGIN);
        }
        return Result.ok(null);
    }

    @PostMapping("changePassword")
    public Result changePassword(@RequestBody ChangePasswordDto data) {
        User user = userService.getById(data.getUid());
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
        captcha.write(response.getOutputStream());
        response.addHeader(SESSION_KEY, captcha.getCode());
    }

}
