package com.software.hdfs.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName BaseDao
 * @Description
 * @Author 徐旭
 * @Date 2018/7/16 11:12
 * @Version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BaseCondition {
    /**
     * 当前页码
     */
    private int pageNum;

    /**
     * 每页条数
     */
    private int pageSize;

    /**
     * 开始查询的字段
     */
    private int pageStart;

    /**
     * 排序字段
     */
    private String sortInfo;

}
