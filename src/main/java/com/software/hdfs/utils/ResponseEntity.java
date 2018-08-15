package com.software.hdfs.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 描述：
 *
 * @ClassName ResponseEntity
 * @Author 徐旭
 * @Date 2018/8/13 15:04
 * @Version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResponseEntity<T> {
    /**
     * 状态：ok 成功，fail 失败
     */
    private String result;

    /**
     * 状态码
     */
    private Integer resCode;

    /**
     * 备注原因
     */
    private String msg;

    /**
     * 返回对象
     */
    private T data;
}
