package com.software.hdfs.po;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author 徐旭
 * @data 2018/7/12 14:56
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BaseQueryPo {
    /**
     * 当前页码
     */
    private int pageNum;

    /**
     * 每页条数
     */
    private int pageSize;
}
