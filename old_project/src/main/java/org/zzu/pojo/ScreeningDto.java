package org.zzu.pojo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @className ScreeingDto
 * @description 封装展示给用户的票务信息
 */
@Data
public class ScreeningDto {
    private Integer sid;

    private String mname;

    private String hname;

    private Date showTime;

    private Date endTime;

    private BigDecimal price;

    private Integer remainingSeats;

    private Integer seatCount;

    private Integer rowCapacity;
}
