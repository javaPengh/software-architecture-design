package org.zzu.pojo;

import lombok.Data;

/**
 * @className LoginDto
 * @description 用户登录表单信息
 */

@Data
public class LoginDto {
    private String username;

    private String password;

    private String captcha;
}
