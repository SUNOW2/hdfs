package com.software.hdfs.utils;

import com.software.hdfs.dao.HdFsMapper;
import com.software.hdfs.domain.HdFsCondition;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.*;
import org.apache.hadoop.io.IOUtils;
import org.apache.hadoop.io.compress.CompressionCodec;
import org.apache.hadoop.io.compress.CompressionCodecFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * 描述：
 *
 * @ClassName HdFsOperation
 * @Author 徐旭
 * @Date 2018/8/13 15:04
 * @Version 1.0
 */
@Slf4j
@Component
@PropertySource({"classpath:/application.yml"})
public class HdFsOperation {

    @Autowired
    private HdFsMapper hdFsMapper;

    @Value("${server.host}")
    private String serverHost;

    @Value("${hdfs.fs.defaultFS}")
    private String host;

    @Value("${hdfs.dfs.nameservices}")
    private String regex;

    @Autowired
    private HdFsConnection hdFsConnection;

    /**
     * 将文件存入HdFs文件系统，并将其相关信息存入数据库
     *
     * @param path
     * @param requestParser
     * @return
     */
    public String storeInData(String path, RequestParser requestParser) {

        FileSystem fs = hdFsConnection.getFSConnection();
        // 文件在hdfs文件系统中的位置及名称,等到后期，直接使用用户的用命名作为文件夹
        String fileName = "/xuxu/" + UUID.randomUUID().toString().replaceAll("_", "+") + "_" + requestParser.getOriginalFilename();
        String srcPath = path + requestParser.getUuid() + "/" + requestParser.getOriginalFilename();

        try {
            fs.copyFromLocalFile(new Path(srcPath), new Path(fileName));
            HdFsCondition hdFsCondition = new HdFsCondition();

            hdFsCondition.setOldName(requestParser.getOriginalFilename());
            hdFsCondition.setNewName(fileName);
            hdFsCondition.setIsDel(1);
            hdFsCondition.setDate(new Date());
            hdFsCondition.setHdFsNo(hdFsMapper.createNo());

            hdFsMapper.saveRecord(hdFsCondition);
        } catch (IOException e) {
            log.error("文件存入HdFs文件系统失败或文件信息存入数据库失败");
            e.printStackTrace();
        } finally {
            close(fs);
        }

        String uri = serverHost + fileName;
        return uri;
    }

    /**
     * 获取文件夹下的文件列表
     *
     * @param srcPath
     * @return
     */
    public List<HdFsCondition> getFileInFolder(String srcPath) {

        FileSystem fs = hdFsConnection.getFSConnection();
        Path path = new Path(srcPath);
        try {
            List<String> list = new ArrayList<>();

            // 获取文件列表
            if (fs.exists(path)) {
                for (FileStatus status : fs.listStatus(path)) {
                    if (status.isDirectory()) {
                        list.add(status.getPath().toString().split(regex)[1] + "_isDirectory");
                    } else {
                        list.add(status.getPath().toString().split(regex)[1]);
                    }
                }
            }

            // 获取文件原名，此部分文件指的是用户通过浏览器上传的文件，该文件在数据库中有记录，还有一部分文件是用户上传的文件夹中的文
            // 件，该文件夹在数据库中有记录，而该文件夹中具体文件在数据库中没有记录
            List<HdFsCondition> hdList = hdFsMapper.queryBatch(list);
            List<String> hdNewList = hdList.stream().map(HdFsCondition::getNewName).collect(Collectors.toList());

            // 获取每一个文件对应的文件原名和其在文件系统的名称
            list.stream().forEach(fileName -> {
                if (!hdNewList.contains(fileName)) {
                    HdFsCondition condition = new HdFsCondition();
                    condition.setNewName(fileName);
                    condition.setOldName(fileName.split(srcPath + "/")[1]);

                    hdList.add(condition);
                }
            });

            return hdList;
        } catch (IOException e) {
            log.error("获取文件列表失败");
            e.printStackTrace();
        } finally {
            close(fs);
        }

        return null;
    }


    /**
     * 创建目录，主要用于创建一个目录
     * 用途1：用户名对应的目录
     * 用途2：文件转存时使用，或者用户浏览器端创建目录
     *
     * @param srcPath
     */
    public boolean createFolder(String srcPath) {

        FileSystem fs = hdFsConnection.getFSConnection();

        try {
            Path path = new Path(srcPath);
            if (fs.exists(path)) {
                log.info("文件夹已经存在");
                return true;
            }

            fs.mkdirs(path);

            HdFsCondition condition = new HdFsCondition();
            condition.setHdFsNo(hdFsMapper.createNo());
            condition.setOldName(StringOperation.splitString(srcPath, "/"));
            condition.setNewName(srcPath);
            condition.setIsDel(1);
            condition.setDate(new Date());

            hdFsMapper.saveRecord(condition);
        } catch (IOException e) {
            log.error("创建目录失败");
            e.printStackTrace();
        } finally {
            close(fs);
        }
        return true;
    }

    /**
     * 从HdFs文件系统下载文件至发送请求的用户
     *
     * @param srcPath
     * @param request
     * @param response
     * @throws IOException
     */
    public void downloadFile(String srcPath, HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException {

        FSDataInputStream fds = hdFsConnection.getFDSConnection(srcPath);
        ResumeBreakPoint resume = getRange(srcPath, request);

        // 请求参数
        String queryString = request.getQueryString();

        // 文件名
        String newName = queryString.split("=")[1];
        HdFsCondition condition = new HdFsCondition();
        condition.setIsDel(1);
        condition.setNewName(newName);

        String fileName = URLEncoder.encode(hdFsMapper.selectRecord(condition).getOldName(), "UTF-8");

        // 文件类型
        String contentType = request.getServletContext().getMimeType(fileName);

        response.setHeader("Accept-Ranges", "bytes");
        // 状态码设为206
        response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);
        response.setHeader("Content-Type", contentType);
        response.setHeader("Content-Length", String.valueOf(resume.getContentLength()));
        response.setHeader("Content-Range", "bytes " + resume.getStartByte() + "-" + resume.getEndByte() + "/" + getFileSize(srcPath));

        // 清除首部的空白行，必须加上这句，不然无法在chrome中使用,另外放在Content-Disposition属性之前，不然无法设置文件名
        response.reset();
        response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");

        // 已传送数据大小
        long transmitted = 0;
        try {
            long contentLength = resume.getContentLength();
            int length = 0;
            byte[] buf = new byte[4096];
            OutputStream os = response.getOutputStream();

            while ((transmitted + length) <= contentLength && (length = fds.read(buf)) != -1) {
                os.write(buf);
                transmitted += length;
            }
            //处理不足buff.length部分
            if (transmitted < contentLength) {
                length = fds.read(buf, 0, (int) (contentLength - transmitted));
                os.write(buf, 0, length);
                transmitted += length;
            }
            os.close();
            fds.close();
            System.out.println("下载完毕：" + resume.getStartByte() + "-" + resume.getEndByte() + "：" + transmitted);
        } catch (IOException e) {
            log.error("用户停止下载" + fileName + "文件");
        }
    }

    /**
     * 主要用于下载的断点续传，获取前端传递的range
     *
     * @param srcPath
     * @param request
     */
    public ResumeBreakPoint getRange(String srcPath, HttpServletRequest request) {
        long startByte = 0;
        long endByte = getFileSize(srcPath) - 1;
        String range = request.getHeader("Content-Range");
        ResumeBreakPoint resume = new ResumeBreakPoint();

        if (range != null && range.contains("bytes=") && range.contains("-")) {
            range = range.substring(range.lastIndexOf("=") + 1).trim();
            String[] ranges = range.split("-");
            try {
                // 判断range类型
                if (ranges.length == 1) {
                    if (range.startsWith("-")) {
                        endByte = Long.parseLong(ranges[0]);
                    } else if (range.endsWith("-")) {
                        startByte = Long.parseLong(ranges[0]);
                    }
                } else if (ranges.length == 2) {
                    startByte = Long.parseLong(ranges[0]);
                    endByte = Long.parseLong(ranges[1]);
                }

            } catch (NumberFormatException e) {
                startByte = 0;
                endByte = getFileSize(srcPath) - 1;
            }
        }

        // 要下载的长度
        long contentLength = endByte - startByte + 1;
        resume.setStartByte(startByte);
        resume.setEndByte(endByte);
        resume.setContentLength(contentLength);
        return resume;
    }

    /**
     * 删除文件或文件夹
     *
     * @param srcPath
     */
    public void delete(String srcPath) {
        FileSystem fs = hdFsConnection.getFSConnection();

        try {
            fs.delete(new Path(srcPath), true);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            log.error("删除文件或文件夹失败");
            close(fs);
        }
    }

    /**
     * 获取文件大小
     *
     * @param srcPath
     * @return
     */
    public long getFileSize(String srcPath) {
        long size = 1L;
        FileSystem fs = hdFsConnection.getFSConnection();

        try {
            size = fs.getContentSummary(new Path(srcPath)).getLength();
        } catch (IOException e) {
            log.error("获取文件大小失败");
            e.printStackTrace();
        } finally {
            close(fs);
        }

        return size;
    }

    /**
     * 文件重命名
     * srcPath: 原名称
     * destPath: 新名称
     *
     * @param srcPath
     * @param destPath
     * @return
     */
    public boolean fileRename(String srcPath, String destPath) {
        FileSystem fs = hdFsConnection.getFSConnection();

        try {
            boolean result = fs.rename(new Path(srcPath), new Path(destPath));
            return result;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * 解压缩文件，但是存在一个问题，即只能解压单个文件压缩而成的文件
     *
     * @param srcPath
     * @param response
     */
    public void decompress(String srcPath, HttpServletResponse response) {

        FileSystem fs = hdFsConnection.getFSConnection();
        Configuration con = hdFsConnection.init();
        String resourcePath = host + srcPath;

        HdFsCondition condition = new HdFsCondition();
        condition.setNewName(srcPath);

        Path inPath = new Path(resourcePath);
        CompressionCodecFactory factory = new CompressionCodecFactory(con);

        InputStream in = null;
        OutputStream out = null;

        try {
            String fileName = "";
            int length = 0;
            String oldName = hdFsMapper.selectRecord(condition).getOldName();
            CompressionCodec codec = factory.getCodec(inPath);

            if (codec == null) {

                // 下面这段代码是存在问题的
                FSDataInputStream fds = hdFsConnection.getFDSConnection(srcPath);
                ZipInputStream zipInputStream = new ZipInputStream(fds);
                ZipEntry zipEntry = null;


                while ((zipEntry = zipInputStream.getNextEntry()) != null) {

                    response.setHeader("Content-Disposition", "attachment; filename=\"" + zipEntry.getName() + "\"");
                    out = response.getOutputStream();
                    byte[] buf = new byte[4096];
                    System.out.println("文件名：" + zipEntry.getName());

                    while ((length = zipInputStream.read(buf)) != -1) {
                        out.write(buf);
                    }
                }

                System.out.println("解压缩结束");

                fds.close();
            } else {
                fileName = CompressionCodecFactory.removeSuffix(oldName, codec.getDefaultExtension());
                response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");

                in = codec.createInputStream(fs.open(inPath));
                out = response.getOutputStream();
                IOUtils.copyBytes(in, out, 1024, false);
            }

        } catch (IOException e) {
            log.error("解压缩文件失败");
            e.printStackTrace();
        } finally {
            IOUtils.closeStream(in);
            IOUtils.closeStream(out);
            close(fs);
        }
    }

    /**
     * 在线解压，获取压缩文件中文件列表
     *
     * @param srcPath
     */
    public void getFileList(String srcPath) {

    }

    /**
     * 将其他用户的文件转储在自己的账户
     *
     * @param srcPath
     * @param destPath
     */
    public void storeFileFromOthers(String srcPath, String destPath) {
        FileSystem fs = hdFsConnection.getFSConnection();

        FSDataInputStream srcStream = null;
        FSDataOutputStream destStream = null;

        try {
            srcStream = fs.open(new Path(srcPath));
            destStream = fs.create(new Path(destPath), true);

            IOUtils.copyBytes(srcStream, destStream, 1024, false);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeStream(srcStream);
            IOUtils.closeStream(destStream);
            close(fs);
        }
    }

    /**
     * 将其他用户的文件或者文件夹转储在自己的账户
     * srcPath: 资源原位置，即拥有该资源的用户
     * destPath: 复制后的路径，即转储资源的用户
     *
     * @param srcPath
     * @param destPath
     */
    public void storeFolderFromOthers(String srcPath, String destPath) {
        FileSystem fs = hdFsConnection.getFSConnection();

        try {
            Path path = new Path(srcPath);
            String destFilePath = destPath + "/" + StringOperation.splitString(srcPath, "/");

            // 判断是文件夹操作还是文件操作
            if (fs.getFileStatus(path).isDirectory()) {
                createFolder(destFilePath);

                // 遍历文件夹中的文件
                for (FileStatus status : fs.listStatus(path)) {
                    String srcFilePath = status.getPath().toString().split(regex)[1];

                    // 递归文件夹，创建文件夹或者存储文件
                    if (status.isDirectory()) {

                        storeFolderFromOthers(srcFilePath, destFilePath);

                    } else {
                        String copyFilePath = destFilePath + "/" + StringOperation.splitString(srcFilePath, "/");

                        storeFileFromOthers(srcFilePath, copyFilePath);
                    }
                }
            } else {
                storeFileFromOthers(srcPath, destFilePath);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            close(fs);
        }
    }

    /**
     * 关闭FileSystem连接
     *
     * @param fs
     */
    public void close(FileSystem fs) {
        try {
            fs.close();
        } catch (IOException e) {
            log.error("关闭FileSystem连接失败");
            e.printStackTrace();
        }
    }

    /**
     * 描述：该类用于存储被下载文件的起始位置和结束位置
     *
     * @ClassName HdFsOperation
     * @Author 徐旭
     * @Date 2018/8/21 13:58
     * @Version 1.0
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    private class ResumeBreakPoint {
        long startByte;
        long endByte;
        long contentLength;
    }

}
