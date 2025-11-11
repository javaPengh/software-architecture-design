package org.zzu.pojo;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import lombok.Data;

/**
 * @TableName hall
 */
@TableName(value ="hall")
@Data
public class Hall implements Serializable {
    @TableId
    private Integer hid;

    private String hname;

    private Integer rowNum;

    private Integer rowCapacity;

    private String screenType;
    @Version
    private Integer version;
    @TableLogic
    private Integer isDeleted;

    private static final long serialVersionUID = 1L;
}