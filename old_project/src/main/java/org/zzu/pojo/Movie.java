package org.zzu.pojo;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

/**
 * @TableName movie
 */
@TableName(value ="movie")
@Document(indexName = "movies")  // 添加ES注解
@Data
public class Movie implements Serializable {
    @TableId
    @org.springframework.data.annotation.Id  // ES的Id注解
    private Integer mid;

    @Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_smart")
    private String mname;

    @Field(type = FieldType.Keyword)
    private String director;

    @Field(type = FieldType.Date, format = DateFormat.year_month_day)
    private LocalDate releaseDate;

    @Field(type = FieldType.Text, analyzer = "ik_max_word")
    private String synopsis;

    private String poster;

    @Field(type = FieldType.Keyword)
    private String type;

    private Integer runtime;
    @Version
    private Integer version;
    @TableLogic
    private Integer isDeleted;

    private static final long serialVersionUID = 1L;
}