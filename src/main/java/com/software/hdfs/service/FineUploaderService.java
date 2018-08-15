package com.software.hdfs.service;

import com.software.hdfs.utils.FineUploadResults;
import com.software.hdfs.utils.RequestParser;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;

/**
 * 描述：静态方法和内部类未重构
 *
 * @ClassName HdFsService
 * @Author 徐旭
 * @Date 2018/8/11 18:46
 * @Version 1.0
 */
@Component
public interface FineUploaderService {
    void init() throws ServletException;

    void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException;

    void handleDeleteFileRequest(String uuid, HttpServletResponse resp) throws IOException;

    FineUploadResults uploadFile(HttpServletRequest req, HttpServletResponse resp) throws IOException;

    void writeFileForNonMultipartRequest(HttpServletRequest req, RequestParser requestParser) throws Exception;

    String writeFileForMultipartRequest(RequestParser requestParser) throws Exception;

    File mergeFiles(File outputFile, File partFile) throws IOException;

    File writeFile(InputStream in, File out, Long expectedFileSize) throws IOException;

    void writeResponse(PrintWriter writer, String failureReason, boolean isIframe, boolean restartChunking, RequestParser requestParser);
}
