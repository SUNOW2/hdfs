package com.software.hdfs.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 描述：
 *
 * @ClassName HdFsCondition
 * @Author 徐旭
 * @Date 2018/8/11 17:56
 * @Version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HdFsCondition extends Object {

    /**
     * 文件编号
     */
    private String hdFsNo;

    /**
     * 文件原名称
     */
    private String oldName;

    /**
     * 文件新名称
     */
    private String newName;

    /**
     * 删除与否
     */
    private Integer isDel;

    /**
     * 文件上传时间，@JsonFormat用于将TimeStamp格式转换成"yyyy-MM-dd HH:mm:ss"格式
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date date;
}
