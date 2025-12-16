package org.zzu.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;

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
