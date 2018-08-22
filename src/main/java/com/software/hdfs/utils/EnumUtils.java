package com.software.hdfs.utils;

/**
 * 描述：
 *
 * @ClassName EnumUtils
 * @Author 徐旭
 * @Date 2018/8/17 13:26
 * @Version 1.0
 */
public enum EnumUtils {
    /**
     * 请求成功
     */
    RESCODE_SUCCESS(200),

    /**
     * 请求失败
     */
    RESCODE_FAIL(201);

    private int resCode;

    public int getResCode() {
        return resCode;
    }

    public void setResCode(int resCode) {
        this.resCode = resCode;
    }

    private EnumUtils(int resCode) {
        this.resCode = resCode;
    }
}
