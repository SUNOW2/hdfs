package com.software.hdfs.utils;

import com.software.hdfs.dao.HdFsMapper;
import com.software.hdfs.domain.HdFsCondition;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;

/**
 * 描述：
 *
 * @ClassName HdFsOperation
 * @Author 徐旭
 * @Date 2018/8/13 15:04
 * @Version 1.0
 */
@Component
@PropertySource({"classpath:/application.yml"})
public class HdFsOperation {

    @Autowired
    private HdFsMapper hdFsMapper;

    @Value("${server.host}")
    private String serverHost;

    @Autowired
    private HdFsConnection hdFsConnection;

    /**
     * 将文件存入hdfs文件系统，并将其相关信息存入数据库
     *
     * @param path
     * @param requestParser
     * @return
     */
    public String StoreInData(String path, RequestParser requestParser) {

        FileSystem fs = hdFsConnection.getFSConnection();
        // 存在hdfs文件系统中的位置及名称,等到后期，直接使用用户的用命名作为文件夹
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
            e.printStackTrace();
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

            if (fs.exists(path)) {
                for (FileStatus status : fs.listStatus(path)) {
                    list.add(status.getPath().toString().split("9000")[1]);
                }
            }
            List<HdFsCondition> HdList = hdFsMapper.queryBatch(list);
            return HdList;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 创建目录，主要用于创建一个根据用户名命名的目录
     *
     * @param srcPath
     */
    public boolean createFolder(String srcPath) {

        FileSystem fs = hdFsConnection.getFSConnection();

        try {
            Path path = new Path(srcPath);
            if (fs.exists(path)) {
                return true;
            }
            fs.mkdirs(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    /**
     * 从HdFs文件系统下载文件至发送请求的用户
     *
     * @param fds
     * @param request
     * @param response
     * @throws IOException
     */
    public void downloadFile(FSDataInputStream fds, HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException {
        // 请求参数
        String queryString = request.getQueryString();

        // 文件名
        String newName = queryString.split("=")[1];
        HdFsCondition condition = new HdFsCondition();
        condition.setNewName(newName);

        String fileName = URLEncoder.encode(hdFsMapper.selectRecord(condition).getOldName(), "UTF-8");

        // 文件类型
        String contentType = request.getServletContext().getMimeType(fileName);

        response.setHeader("Accept-Ranges", "bytes");
        // 状态码设为206
        response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);
        response.setHeader("Content-Type", contentType);

        // 清除首部的空白行，必须加上这句，不然无法在chrome中使用,另外放在Content-Disposition属性之前，不然无法设置文件名
        response.reset();
        response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");

        try {
            int length = 0;
            byte[] buf = new byte[4096];
            OutputStream os = response.getOutputStream();

            while ((length = fds.read(buf)) != -1) {
                os.write(buf);
            }
            os.close();
            fds.close();
        } catch (IOException e) {
            System.out.println("用户停止下载" + fileName + "文件");
        }
    }
}
