package org.zzu.dto;

import lombok.Data;

@Data
public class ChangePasswordDto {
    private Integer uid;
    private String oldPwd;
    private String newPwd;
}
