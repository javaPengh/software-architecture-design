package org.zzu.vo;

import lombok.Data;
import java.util.Date;

@Data
public class PortalVo {
    private String keyword;
    private Integer id;
    private Date date;
    private Object orderStatus;
    private Integer pageNum = 1;
    private Integer pageSize = 10;
}
