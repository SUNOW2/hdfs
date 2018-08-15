package com.software.hdfs.form;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 描述：
 *
 * @ClassName QueryForm
 * @Author 徐旭
 * @Date 2018/8/14 10:56
 * @Version 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class QueryForm {
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
     * 文件上传时间
     */
    private Date date;
}
