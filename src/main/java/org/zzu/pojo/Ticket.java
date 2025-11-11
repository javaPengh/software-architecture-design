package org.zzu.pojo;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;

/**
 * @TableName ticket
 */
@TableName(value ="ticket")
@Data
public class Ticket implements Serializable {
    @TableId
    private Integer tid;

    private Integer sid;

    private Integer uid;

    private Integer seatNumber;

    private Date purchaseTime;

    private BigDecimal price;

    private Object orderStatus;
    @Version
    private Integer version;
    @TableLogic
    private Integer isDeleted;

    private static final long serialVersionUID = 1L;
}