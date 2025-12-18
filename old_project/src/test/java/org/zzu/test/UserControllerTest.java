package org.zzu.test;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.zzu.controller.UserController;
import org.zzu.mapper.UserMapper;
import org.zzu.pojo.ChangePasswordDto;
import org.zzu.pojo.LoginDto;
import org.zzu.pojo.User;
import org.zzu.service.UserService;
import org.zzu.utils.JwtHelper;
import org.zzu.utils.MD5Util;
import org.zzu.utils.Result;
import org.zzu.utils.ResultCodeEnum;


import java.util.concurrent.TimeUnit;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@Slf4j
public class UserControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UserService userService;

    @Mock
    private JwtHelper jwtHelper;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ValueOperations<String, Object> valueOperations;

    @InjectMocks
    private UserController userController;

    @Resource
    private UserMapper userMapper;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();

        // 设置Redis模板的模拟行为
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    }

    @Test
    public void testSelectUser() {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUsername, "user");
        Long count = userMapper.selectCount(queryWrapper);
        log.info("用户名为user的用户个数:" + count);
    }

    @Test
    public void testLogin_CaptchaError() throws Exception {
        LoginDto loginDto = new LoginDto();
        loginDto.setUsername("testuser");
        loginDto.setPassword("password");
        loginDto.setCaptcha("WRONG");

        mockMvc.perform(post("/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Captcha", "ABCD")
                        .content(asJsonString(loginDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ResultCodeEnum.CAPTCHA_ERROR.getCode()));
    }

    @Test
    public void testLogout_Success() throws Exception {
        // 模拟认证上下文
        User user = new User();
        user.setUid(1); // Integer 类型

        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(user);

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        mockMvc.perform(post("/user/logout"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        // 修正键名为 "login:uid:1"
        verify(redisTemplate).delete("login:uid:" + 1);
    }

    @Test
    public void testGetUserInfo_NotLoggedIn() throws Exception {
        // 模拟未登录状态
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(null);
        SecurityContextHolder.setContext(securityContext);

        mockMvc.perform(get("/user/getUserInfo"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ResultCodeEnum.NOTLOGIN.getCode()));
    }

    @Test
    public void testGetUserInfo_Success() throws Exception {
        // 模拟认证上下文
        User user = new User();
        user.setUid(1); // Integer 类型
        user.setUsername("testuser");
        user.setPassword("password");
        user.setNickname("Test User");

        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(user);

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        mockMvc.perform(get("/user/getUserInfo"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.loginUser.username").value("testuser"))
                .andExpect(jsonPath("$.data.loginUser.password").doesNotExist()); // 密码不应返回
    }

    @Test
    public void testCheckUserName() throws Exception {
        String username = "testuser";

        mockMvc.perform(post("/user/checkUsername/" + username))
                .andExpect(status().isOk());

        verify(userService).checkUserName(username);
    }

    @Test
    public void testRegister_UserExists() throws Exception {
        User user = new User();
        user.setUsername("existinguser");
        user.setPassword("password");

        when(userService.register(user)).thenReturn(
                Result.build(null, ResultCodeEnum.USERNAME_USED));

        mockMvc.perform(post("/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ResultCodeEnum.USERNAME_USED.getCode()));
    }

    @Test
    public void testChangePassword_Success() throws Exception {
        ChangePasswordDto dto = new ChangePasswordDto();
        dto.setUid(1); // Integer 类型
        dto.setOldPwd("oldpassword");
        dto.setNewPwd("newpassword");

        User user = new User();
        user.setUid(1); // Integer 类型
        user.setPassword(MD5Util.encrypt("oldpassword"));

        when(userService.getById(1)).thenReturn(user);

        mockMvc.perform(post("/user/changePassword")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        verify(userService).getById(1);
        verify(userService).updateById(argThat(u ->
                u.getUid().equals(1) &&
                        u.getPassword().equals(MD5Util.encrypt("newpassword"))));
    }

    @Test
    public void testChangePassword_UserNotFound() throws Exception {
        ChangePasswordDto dto = new ChangePasswordDto();
        dto.setUid(999); // 不存在的用户ID
        dto.setOldPwd("oldpassword");
        dto.setNewPwd("newpassword");

        when(userService.getById(999)).thenReturn(null);

        mockMvc.perform(post("/user/changePassword")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ResultCodeEnum.SYSTEM_ERROR.getCode()));
    }

    @Test
    public void testGenerateCaptcha() throws Exception {
        mockMvc.perform(get("/user/captcha"))
                .andExpect(status().isOk())
                .andExpect(header().exists("Captcha"));
    }

    // 工具方法：将对象转换为JSON字符串
    private static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
