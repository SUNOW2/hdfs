package com.software.hdfs.utils;

import java.util.Arrays;
import java.util.List;

/**
 * 描述：
 *
 * @ClassName StringOperation
 * @Author 徐旭
 * @Date 2018/8/22 16:54
 * @Version 1.0
 */
public class StringOperation {

    /**
     * 切割字符串，并返回最后一个子字符串
     *
     * @param string
     * @param regex
     * @return
     */
    public static String splitString(String string, String regex) {
        List<String> list = Arrays.asList(string.split(regex));

        return list.get(list.size() - 1);
    }
}
