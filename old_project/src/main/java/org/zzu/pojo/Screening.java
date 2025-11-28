package org.zzu.pojo;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;

/**
 * @TableName screening
 */
@TableName(value ="screening")
@Data
public class Screening implements Serializable {
    @TableId
    private Integer sid;

    private Integer hid;

    private Integer mid;

    private Date showTime;

    private Date endTime;

    private BigDecimal price;

    private Integer seatCount;

    private Integer remainingSeats;
    @Version
    private Integer version;
    @TableLogic
    private Integer isDeleted;
    @Serial
    private static final long serialVersionUID = 1L;
}