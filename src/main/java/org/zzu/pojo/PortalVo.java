package org.zzu.pojo;

import lombok.Data;

import java.util.Date;

/**
 * @className PortalVo
 * @description 数据查询参数
 */
@Data
public class PortalVo {
    private String keyword;
    private Integer id;
    private Date date;
    private Object orderStatus;
    private Integer pageNum = 1;
    private Integer pageSize = 10;
}
