// 正确的文件内容：src/main/java/org/zzu/service/impl/UserDetailsServiceImpl.java

package org.zzu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.zzu.mapper.UserMapper;
import org.zzu.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

// ↓↓↓↓ 确保这里的类名是 UserDetailsServiceImpl ↓↓↓↓
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 根据用户名查询数据库
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUsername, username);
        User user = userMapper.selectOne(queryWrapper);

        // 如果用户不存在，必须抛出此异常
        if (user == null) {
            throw new UsernameNotFoundException("用户名或密码错误");
        }

        // 查询到用户，返回一个UserDetails对象（我们已经让User类实现了该接口）
        return user;
    }
}