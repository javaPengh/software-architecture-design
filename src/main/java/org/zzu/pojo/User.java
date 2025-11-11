package org.zzu.pojo;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * @TableName user
 */
@TableName(value ="user")
@Data
public class User implements Serializable {
    @TableId
    private Integer uid;

    private String username;

    private String password;

    private String nickname;

    private String phone;

    private Object type;

    private Date registerTime;
    @Version
    private Integer version;
    @TableLogic
    private Integer isDeleted;

    private static final long serialVersionUID = 1L;
}