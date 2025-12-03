package org.zzu.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.zzu.pojo.User;
import org.zzu.utils.Result;

/**
 * @description 针对表【user】的数据库操作Service
 */
public interface UserService extends IService<User> {

    /**
     * 检查账号是否可以注册
     *
     * @param username 账号信息
     * @return Result
     */
    Result checkUserName(String username);

    /**
     * 用户注册
     * @param user 用户信息
     * @return Result
     */
    Result register(User user);

    // login(LoginDto loginDto) 方法声明已从此接口中移除
    // getUserInfo(String token) 方法声明已从此接口中移除
}