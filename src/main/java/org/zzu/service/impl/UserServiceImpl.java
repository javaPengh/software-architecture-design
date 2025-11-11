package org.zzu.service.impl;

import com.alibaba.druid.util.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.zzu.pojo.LoginDto;
import org.zzu.pojo.User;
import org.zzu.service.UserService;
import org.zzu.mapper.UserMapper;
import org.zzu.utils.JwtHelper;
import org.zzu.utils.MD5Util;
import org.zzu.utils.Result;
import org.zzu.utils.ResultCodeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
* @description 针对表【user】的数据库操作Service实现
*/
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService{

    @Autowired
    private JwtHelper jwtHelper;
    @Autowired
    private  UserMapper userMapper;

    /**
     * 登录业务实现
     * @param loginDto
     * @return result封装
     */
    @Override
    public Result login(LoginDto loginDto) {

        //根据账号查询
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUsername,loginDto.getUsername());
        User loginUser = userMapper.selectOne(queryWrapper);

        //账号判断
        if (loginUser == null) {
            //账号错误
            return Result.build(null, ResultCodeEnum.USERNAME_ERROR);
        }

        //判断密码
        if (!StringUtils.isEmpty(loginDto.getPassword())
                && loginUser.getPassword().equals(MD5Util.encrypt(loginDto.getPassword())))
        {
            //账号密码正确
            //根据用户唯一标识生成token
            String token = jwtHelper.createToken(Long.valueOf(loginUser.getUid()));

            Map data = new HashMap();
            data.put("token",token);

            return Result.ok(data);
        }

        //密码错误
        return Result.build(null,ResultCodeEnum.PASSWORD_ERROR);
    }

    /**
     * 查询用户数据
     * @param token
     * @return result封装
     */
    @Override
    public Result getUserInfo(String token) {

        //1.判定是否有效期
        if (jwtHelper.isExpiration(token)) {
            //true过期,直接返回未登录
            return Result.build(null,ResultCodeEnum.NOTLOGIN);
        }

        //2.获取token对应的用户
        int userId = jwtHelper.getUserId(token).intValue();

        //3.查询数据
        User user = userMapper.selectById(userId);

        if (user != null) {
            user.setPassword(null);
            Map data = new HashMap();
            data.put("loginUser",user);
            return Result.ok(data);
        }

        return Result.build(null,ResultCodeEnum.NOTLOGIN);
    }
    /**
     * 检查账号是否可以注册
     *
     * @param username 账号信息
     * @return Result
     */
    @Override
    public Result checkUserName(String username) {

        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUsername,username);
        User user = userMapper.selectOne(queryWrapper);

        if (user != null){
            return Result.build(null,ResultCodeEnum.USERNAME_USED);
        }

        return Result.ok(null);
    }
    @Override
    public Result register(User user) {
        if(user.getNickname()==null){
            user.setNickname(user.getUsername());
        }
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUsername,user.getUsername());
        Long count = userMapper.selectCount(queryWrapper);

        if (count > 0){
            return Result.build(null,ResultCodeEnum.USERNAME_USED);
        }

        user.setPassword(MD5Util.encrypt(user.getPassword()));
        int rows = userMapper.insert(user);
        System.out.println("rows = " + rows);
        return Result.ok(null);
    }
}




