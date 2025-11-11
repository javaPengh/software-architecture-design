package org.zzu.service;

import org.zzu.pojo.LoginDto;
import org.zzu.pojo.User;
import com.baomidou.mybatisplus.extension.service.IService;
import org.zzu.utils.Result;

/**
* @description 针对表【user】的数据库操作Service
*/
public interface UserService extends IService<User> {

    Result login(LoginDto user);

    Result getUserInfo(String token);

    Result checkUserName(String username);

    Result register(User user);

}
