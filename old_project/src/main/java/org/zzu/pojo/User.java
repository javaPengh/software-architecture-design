package org.zzu.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Data
@TableName("user")
public class User implements UserDetails, Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "uid", type = IdType.AUTO)
    private Integer uid;

    private String username;

    private String password;

    // <-- 关键点 1: 确保这里是 `type` 字段，以匹配您数据库的 `type` 列
    private String type;

    private String nickname;

    private String userPic;

    private String phone;


    // ↓↓↓↓ 关键点 2: 确保 getAuthorities 方法从 `type` 字段读取角色信息 ↓↓↓↓
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (this.type == null || this.type.trim().isEmpty()) {
            return Collections.emptyList();
        }
        // 将数据库中的角色字符串 (如 "admin" 或 "normal") 转换为 Spring Security 能识别的权限对象
        List<GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + this.type.toUpperCase()));
        return authorities;
    }

    // --- UserDetails 接口的其他方法 ---
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}