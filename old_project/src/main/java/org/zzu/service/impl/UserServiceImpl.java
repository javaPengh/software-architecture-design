package org.zzu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.zzu.mapper.UserMapper;
import org.zzu.pojo.User;
import org.zzu.service.UserService;
import org.zzu.utils.MD5Util;
import org.zzu.utils.Result;
import org.zzu.utils.ResultCodeEnum;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
        implements UserService {

    // 正确的位置：在类的内部，作为第一个成员变量
    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    private UserMapper userMapper;

    @Override
    public Result checkUserName(String username) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUsername, username);
        User user = userMapper.selectOne(queryWrapper);

        if (user != null) {
            return Result.build(null, ResultCodeEnum.USERNAME_USED);
        }
        return Result.ok(null);
    }

    // 带有完整日志的 register 方法
    @Override
    public Result register(User user) {
        // 1. 在方法入口打印接收到的整个 user 对象
        log.info("接收到注册请求，传入的User对象: {}", user);

        if (user.getNickname() == null || user.getNickname().isEmpty()) {
            user.setNickname(user.getUsername());
        }

        // 2. 增加对 username 的 null 检查，防止空指针和错误的数据库查询
        if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
            log.error("注册失败：传入的用户名为空！");
            return Result.build(null, ResultCodeEnum.USERNAME_ERROR); // 可以返回一个更具体的错误码
        }

        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUsername, user.getUsername());

        Long count;
        try {
            count = userMapper.selectCount(queryWrapper);
        } catch (Exception e) {
            log.error("数据库查询用户数量时发生异常！", e);
            return Result.build(null, ResultCodeEnum.SYSTEM_ERROR);
        }

        // 3. 打印从数据库查询到的数量
        log.info("数据库查询结果：用户名为 '{}' 的记录有 {} 条", user.getUsername(), count);

        if (count > 0) {
            log.warn("用户名 '{}' 已被占用，注册被拒绝。", user.getUsername());
            return Result.build(null, ResultCodeEnum.USERNAME_USED);
        }

        user.setPassword(MD5Util.encrypt(user.getPassword()));
        this.save(user);
        log.info("用户 '{}' 注册成功！", user.getUsername());
        return Result.ok(null);
    }
}