package org.zzu.pojo;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * @TableName movie
 */
@TableName(value ="movie")
@Data
public class Movie implements Serializable {
    @TableId
    private Integer mid;

    private String mname;

    private String director;

    private Date releaseDate;

    private String synopsis;

    private String poster;

    private String type;

    private Integer runtime;
    @Version
    private Integer version;
    @TableLogic
    private Integer isDeleted;

    private static final long serialVersionUID = 1L;
}