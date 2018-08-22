package com.software.hdfs.controller;

import com.software.hdfs.domain.HdFsCondition;
import com.software.hdfs.form.DeleteForm;
import com.software.hdfs.form.QueryForm;
import com.software.hdfs.form.UpdateBatchForm;
import com.software.hdfs.form.UpdateForm;
import com.software.hdfs.service.FineUploaderService;
import com.software.hdfs.service.HdFsService;
import com.software.hdfs.utils.*;
import org.apache.hadoop.fs.FSDataInputStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.List;

/**
 * 描述：
 *
 * @ClassName HdfsController
 * @Author 徐旭
 * @Date 2018/8/9 15:10
 * @Version 1.0
 */
@RestController
@RequestMapping(value = "/hdfs")
public class HdFsController extends BaseController {

    @Autowired
    private HdFsService hdFsService;

    @Autowired
    private FineUploaderService fineUploaderService;

    @Autowired
    private HdFsConnection hdFsConnection;

    @Autowired
    private HdFsOperation hdFsOperation;

    /**
     * 描述：根据文件在HdFs文件系统上所处路径查询文件内容
     *
     * @param filePath
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/getFileContent", method = {RequestMethod.GET, RequestMethod.POST})
    public ResponseEntity getFileContent(String filePath) throws Exception {

        FSDataInputStream fds = hdFsConnection.getFDSConnection(filePath);
        byte[] buff = new byte[1024];
        int length = 0;

        while ((length = fds.read(buff)) != -1) {
            System.out.println(new String(buff, 0, length));
        }
        return this.getSuccessResult("查询文件信息成功");
    }

    /**
     * 描述：通过FineUploader上传文件
     *
     * @param req
     * @param res
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "/uploadFile", method = {RequestMethod.GET, RequestMethod.POST})
    public FineUploadResults uploadFile(HttpServletRequest req, HttpServletResponse res) throws IOException {
        return fineUploaderService.uploadFile(req, res);
    }

    /**
     * 描述：从HdFs文件系统上下载文件
     *
     * @param filePath
     * @param req
     * @param res
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "/downloadFile", method = RequestMethod.GET)
    public ResponseEntity downloadFile(String filePath, HttpServletRequest req, HttpServletResponse res) throws IOException {

        hdFsOperation.downloadFile(filePath, req, res);

        return this.getSuccessResult("下载文件成功");
    }

    /**
     * 描述：从HdFs文件系统上删除文件
     *
     * @param form
     * @return
     */
    @RequestMapping(value = "/deleteFile", method = RequestMethod.POST)
    public ResponseEntity deleteFile(@Valid DeleteForm form) {
        HdFsCondition condition = new HdFsCondition();
        condition.setNewName(form.getFilePath());
        condition.setHdFsNo(form.getHdFsNo());
        condition.setIsDel(0);

        hdFsOperation.delete(form.getFilePath());
        hdFsService.updateRecord(condition);

        return this.getSuccessResult("删除文件成功");
    }

    /**
     * 更新文件信息，主要包括重命名等
     *
     * @param form
     * @return
     */
    @RequestMapping(value = "/updateFile", method = {RequestMethod.GET, RequestMethod.POST})
    public ResponseEntity updateFile(UpdateForm form) {
        HdFsCondition condition = CopyUtils.transfer(form, HdFsCondition.class);

        hdFsService.updateRecord(condition);

        return this.getSuccessResult("更新文件信息成功");
    }

    /**
     * 批量更新操作
     *
     * @param form
     * @return
     */
    @RequestMapping(value = "/updateBatch", method = RequestMethod.POST)
    public ResponseEntity updateBatch(UpdateBatchForm form) {

        hdFsService.updateBatch(form.getList());

        return this.getSuccessResult("更新成功");
    }

    /**
     * 不分页查询数据
     *
     * @param form
     * @return
     */
    @RequestMapping(value = "/selectList", method = {RequestMethod.GET, RequestMethod.POST})
    public ResponseEntity selectList(QueryForm form) {

        HdFsCondition condition = CopyUtils.transfer(form, HdFsCondition.class);
        condition.setIsDel(1);
        List<HdFsCondition> list = hdFsService.selectListRecord(condition);

        return this.getSuccessResult("查询数据成功", list);
    }

    /**
     * 获取文件夹下的文件列表
     *
     * @param srcPath
     * @return
     */
    @RequestMapping(value = "/fileInFolder", method = {RequestMethod.GET, RequestMethod.POST})
    public ResponseEntity fileInFolder(String srcPath) {

        List<HdFsCondition> list = hdFsOperation.getFileInFolder(srcPath);

        return this.getSuccessResult("获取文件夹内数据成功", list);
    }

    @RequestMapping(value = "/test", method = {RequestMethod.GET, RequestMethod.POST})
    public ResponseEntity test(String srcPath, String destPath,HttpServletResponse res) {
//        hdFsOperation.decompress(srcPath, res);
//        hdFsOperation.createFolder("/sunow/te");
//        hdFsOperation.storeFolderFromOthers(srcPath, destPath);
        hdFsOperation.fileRename(srcPath, destPath);
        return this.getSuccessResult("解压缩成功");
    }
}
