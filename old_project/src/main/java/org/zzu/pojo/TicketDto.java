package org.zzu.pojo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @className TicketDto
 * @description 封装购票页面展示给用户的票务信息
 */
@Data
public class TicketDto {
    private Integer tid;

    private String mname;

    private String hname;

    private String seatNumber;

    private Date showTime;

    private Date endTime;

    private Date purchaseTime;

    private BigDecimal price;

    private Object orderStatus;

}