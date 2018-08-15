package com.software.hdfs.utils;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URI;

/**
 * 描述：该类主要用于HdFs的连接操作
 *
 * @ClassName HdFsConnection
 * @Author 徐旭
 * @Date 2018/8/11 10:49
 * @Version 1.0
 */
@Component
public class HdFsConnection {

    @Value("${hdfs.host}")
    private String host;

    @Value("${hdfs.user}")
    private String user;

    private Configuration con = new Configuration();

    /**
     * 获取一个FileSystem实例
     *
     * @param path
     * @return
     */
    public FileSystem getFSConnection(String path) {

        String ResourcePath = host + path;
        FileSystem fs = null;

        try {
            fs = FileSystem.get(new URI(ResourcePath), con, user);
        } catch (Exception e) {
            System.out.println("连接hdfs文件系统失败");
            e.printStackTrace();
        }

        return fs;
    }

    /**
     * 获取一个FileSystem实例
     *
     * @return
     */
    public FileSystem getFSConnection() {

        String ResourcePath = host;
        FileSystem fs = null;

        try {
            fs = FileSystem.get(new URI(ResourcePath), con, user);
        } catch (Exception e) {
            System.out.println("连接hdfs文件系统失败");
            e.printStackTrace();
        }

        return fs;
    }

    /**
     * 获取一个FSDataInputStream实例
     *
     * @param path
     * @return
     */
    public FSDataInputStream getFDSConnection(String path) {

        String ResourcePath = host + path;
        FileSystem fs;
        FSDataInputStream fds = null;

        System.out.println("ResourcePath = " + ResourcePath);

        try {
            fs = FileSystem.get(new URI(ResourcePath), con, user);
            fds = fs.open(new Path(ResourcePath));
        } catch (Exception e) {
            System.out.println("连接hdfs文件系统失败");
            e.printStackTrace();
        }

        return fds;
    }
}
