package com.software.hdfs.utils;

import org.springframework.stereotype.Component;

import java.io.File;

/**
 * 描述：
 *
 * @ClassName DirectoryUtils
 * @Author 徐旭
 * @Date 2018/8/13 13:36
 * @Version 1.0
 */
@Component
public class DirectoryUtils {

    /**
     * 删除文件夹以及子文件
     *
     * @param srcDirectory
     */
    public static void deleteDirectoryOperation(File srcDirectory) {
        if (srcDirectory.isDirectory()) {
            File[] files = srcDirectory.listFiles();
            // 递归删除目录下的子目录下的文件
            for (int i = 0; i < files.length; i++) {
                deleteDirectoryOperation(files[i]);
            }
        }
        srcDirectory.delete();
    }

    /**
     * 递归删除两天内的文件
     *
     * @param srcDirectory
     */
    public static void deleteAllFileOperation(File srcDirectory) {
        if (srcDirectory.isDirectory()) {
            File[] files = srcDirectory.listFiles();
            // 递归删除目录下的子目录下的文件
            for (int i = 0; i < files.length; i++) {
                long middleTime = System.currentTimeMillis() - files[i].lastModified();
                long interval = 1000 * 60 * 60 * 24;
                if (middleTime / interval > 2) {
                    deleteDirectoryOperation(files[i]);
                }
            }
        }
    }


    /**
     * 删除文件
     *
     * @param file
     */
    public static void deleteFileOperation(File file) {
        if (file.exists() && file.isFile()) {
            file.delete();
        }
    }
}
