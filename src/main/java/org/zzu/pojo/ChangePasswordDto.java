package org.zzu.pojo;

import lombok.Data;

/**
 * @className ChangePasswordDto
 * @description 更改密码数据传输对象
 */
@Data
public class ChangePasswordDto {
    private Integer uid;
    private String oldPwd;
    private String newPwd;
}
