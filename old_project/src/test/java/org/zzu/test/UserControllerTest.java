package org.zzu.test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.zzu.controller.UserController;
import org.zzu.pojo.ChangePasswordDto;
import org.zzu.pojo.LoginDto;
import org.zzu.pojo.User;
import org.zzu.service.UserService;
import org.zzu.utils.JwtHelper;
import org.zzu.utils.MD5Util;
import org.zzu.utils.Result;
import org.zzu.utils.ResultCodeEnum;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class UserControllerTest {

    @InjectMocks
    private UserController userController;

    @Mock
    private UserService userService;

    @Mock
    private JwtHelper jwtHelper;

    private MockHttpServletRequest mockRequest;
    private MockHttpServletResponse mockResponse;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockRequest = new MockHttpServletRequest();
        mockResponse = new MockHttpServletResponse();
    }

    @Test
    void testLoginSuccess() {
        // 准备测试数据
        LoginDto loginDto = new LoginDto();
        loginDto.setUsername("testUser");
        loginDto.setPassword("testPassword");
        loginDto.setCaptcha("ABC123");

        mockRequest.addHeader("Captcha", "abc123"); // 头部信息不区分大小写

        Result mockResult = Result.ok(null);
        when(userService.login(any(LoginDto.class))).thenReturn(mockResult);

        // 执行测试
        Result result = userController.login(loginDto, mockRequest);

        // 验证结果
        assertNotNull(result);
        assertEquals(ResultCodeEnum.SUCCESS.getCode(), result.getCode());
        verify(userService, times(1)).login(any(LoginDto.class));
    }

    @Test
    void testLoginWithWrongCaptcha() {
        // 准备测试数据
        LoginDto loginDto = new LoginDto();
        loginDto.setUsername("testUser");
        loginDto.setPassword("testPassword");
        loginDto.setCaptcha("WRONG");

        mockRequest.addHeader("Captcha", "ABC123");

        // 执行测试
        Result result = userController.login(loginDto, mockRequest);

        // 验证结果
        assertNotNull(result);
        assertEquals(ResultCodeEnum.CAPTCHA_ERROR.getCode(), result.getCode());
        verify(userService, never()).login(any(LoginDto.class));
    }

    @Test
    void testGetUserInfo() {
        // 准备测试数据
        String token = "mockToken";
        Result mockResult = Result.ok(new User());

        when(userService.getUserInfo(anyString())).thenReturn(mockResult);

        // 执行测试
        Result result = userController.userInfo(token);

        // 验证结果
        assertNotNull(result);
        assertEquals(ResultCodeEnum.SUCCESS.getCode(), result.getCode());
        verify(userService, times(1)).getUserInfo(anyString());
    }

    @Test
    void testCheckUserName() {
        // 准备测试数据
        String username = "testUser";
        Result mockResult = Result.ok(Boolean.TRUE);

        when(userService.checkUserName(anyString())).thenReturn(mockResult);

        // 执行测试
        Result result = userController.checkUserName(username);

        // 验证结果
        assertNotNull(result);
        assertEquals(ResultCodeEnum.SUCCESS.getCode(), result.getCode());
        verify(userService, times(1)).checkUserName(anyString());
    }

    @Test
    void testRegister() {
        // 准备测试数据
        User user = new User();
        user.setUsername("newUser");
        user.setPassword("password");
        Result mockResult = Result.ok(null);

        when(userService.register(any(User.class))).thenReturn(mockResult);

        // 执行测试
        Result result = userController.register(user);

        // 验证结果
        assertNotNull(result);
        assertEquals(ResultCodeEnum.SUCCESS.getCode(), result.getCode());
        verify(userService, times(1)).register(any(User.class));
    }

    @Test
    void testCheckLoginWithValidToken() {
        // 准备测试数据
        String token = "validToken";

        when(jwtHelper.isExpiration(anyString())).thenReturn(false);

        // 执行测试
        Result result = userController.checkLogin(token);

        // 验证结果
        assertNotNull(result);
        assertEquals(ResultCodeEnum.SUCCESS.getCode(), result.getCode());
    }

    @Test
    void testCheckLoginWithInvalidToken() {
        // 准备测试数据
        String token = "expiredToken";

        when(jwtHelper.isExpiration(anyString())).thenReturn(true);

        // 执行测试
        Result result = userController.checkLogin(token);

        // 验证结果
        assertNotNull(result);
        assertEquals(ResultCodeEnum.NOTLOGIN.getCode(), result.getCode());
    }

    @Test
    void testCheckLoginWithEmptyToken() {
        // 准备测试数据
        String token = "";

        // 执行测试
        Result result = userController.checkLogin(token);

        // 验证结果
        assertNotNull(result);
        assertEquals(ResultCodeEnum.NOTLOGIN.getCode(), result.getCode());
    }

    @Test
    void testChangePasswordSuccess() {
        // 准备测试数据
        ChangePasswordDto data = new ChangePasswordDto();
        data.setUid(1);  // 注意这里改为 int 类型
        data.setOldPwd("oldPassword");
        data.setNewPwd("newPassword");

        User user = new User();
        user.setUid(1);  // User 类中使用的是 uid 字段
        user.setPassword(MD5Util.encrypt("oldPassword"));

        when(userService.getById(eq(1))).thenReturn(user);  // 使用 Integer 类型的 1
        when(userService.updateById(any(User.class))).thenReturn(true);

        // 执行测试
        Result result = userController.changePassword(data);

        // 验证结果
        assertNotNull(result);
        assertEquals(ResultCodeEnum.SUCCESS.getCode(), result.getCode());
        verify(userService, times(1)).getById(eq(1));  // 使用 Integer 类型的 1
        verify(userService, times(1)).updateById(any(User.class));
    }

    @Test
    void testChangePasswordWithWrongOldPassword() {
        // 准备测试数据
        ChangePasswordDto data = new ChangePasswordDto();
        data.setUid(1);  // 注意这里改为 int 类型
        data.setOldPwd("wrongOldPassword");
        data.setNewPwd("newPassword");

        User user = new User();
        user.setUid(1);  // User 类中使用的是 uid 字段
        user.setPassword(MD5Util.encrypt("correctOldPassword"));

        when(userService.getById(eq(1))).thenReturn(user);  // 使用 Integer 类型的 1

        // 执行测试
        Result result = userController.changePassword(data);

        // 验证结果
        assertNotNull(result);
        assertEquals(ResultCodeEnum.PASSWORD_ERROR.getCode(), result.getCode());
        verify(userService, times(1)).getById(eq(1));  // 使用 Integer 类型的 1
        verify(userService, never()).updateById(any(User.class));
    }

    @Test
    void testGenerateCaptcha() throws IOException {
        // 执行测试
        assertDoesNotThrow(() -> {
            userController.generateCaptcha(mockResponse);
        });

        // 验证结果
        assertTrue(mockResponse.getHeaderNames().contains("Captcha"));
        assertNotNull(mockResponse.getContentAsByteArray());
    }
}
