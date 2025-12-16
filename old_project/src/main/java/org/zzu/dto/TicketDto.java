package org.zzu.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;

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
