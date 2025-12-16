package org.zzu.test;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.zzu.dto.ChangePasswordDto;
import org.zzu.dto.LoginDto;
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

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();

        // 设置Redis模板的模拟行为
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    }

    @Test
    public void testLogin_Success() throws Exception {
        // 准备数据
        LoginDto loginDto = new LoginDto();
        loginDto.setUsername("testuser");
        loginDto.setPassword("password");
        loginDto.setCaptcha("ABCD");

        User user = new User();
        user.setUid(1); // Integer 类型
        user.setType("USER");
        user.setNickname("Test User");

        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(user);

        // 模拟依赖项的行为
        when(authenticationManager.authenticate(any())).thenReturn(authentication);
        when(jwtHelper.createToken(1L)).thenReturn("mocked-jwt-token");
        when(jwtHelper.getTokenExpiration()).thenReturn(30L);

        // 执行测试
        mockMvc.perform(post("/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Captcha", "ABCD")
                        .content(asJsonString(loginDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.token").value("mocked-jwt-token"))
                .andExpect(jsonPath("$.data.role").value("USER"))
                .andExpect(jsonPath("$.data.nickname").value("Test User"));

        // 验证交互 - 修正键名为 "login:uid:1"
        verify(authenticationManager).authenticate(any());
        verify(valueOperations).set(
                eq("login:uid:1"), // 修正为实际使用的键名格式
                eq("mocked-jwt-token"),
                eq(30L),
                eq(TimeUnit.MINUTES)
        );
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
        verify(redisTemplate).delete("login:uid:1");
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
