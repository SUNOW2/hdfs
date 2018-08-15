package com.software.hdfs.controller;

import com.software.hdfs.po.BaseQueryPo;
import com.software.hdfs.utils.CentreCutPageResponse;
import com.software.hdfs.utils.CentreListResponse;
import com.software.hdfs.utils.ResponseEntity;

import java.util.Collections;
import java.util.List;

/**
 * 描述：
 *
 * @ClassName BaseController
 * @Author 徐旭
 * @Date 2018/8/12 21:21
 * @Version 1.0
 */
public class BaseController {
    /**
     * 成功的Status Code
     */
    private static final int RESCODE_OK = 200;

    /**
     * 失败的Status Code
     */
    private static final int RESCODE_FAIL = 201;

    /**
     * 描述：获取成功信息
     * @return
     */
    protected ResponseEntity getSuccessResult(String msg) {
        return new ResponseEntity("ok", RESCODE_OK, msg, Collections.EMPTY_MAP);
    }

    /**
     * 描述：获取默认ajax成功信息
     * @return
     */
    protected ResponseEntity getSuccessResult() {
        return getSuccessResult("操作成功");
    }

    /**
     * 描述：获取成功结果
     * @param obj
     * @param <T>
     * @return
     */
    protected <T> ResponseEntity<T> getSuccessResult(T obj) {
        return new ResponseEntity<>("ok", RESCODE_OK, "操作成功", obj);
    }

    /**
     * 描述：获取成功信息和结果
     * @param msg
     * @param obj
     * @param <T>
     * @return
     */
    protected <T> ResponseEntity<T> getSuccessResult(String msg, T obj) {
        return new ResponseEntity<>("ok", RESCODE_OK, "操作成功", obj);
    }


    /**
     * 描述：获取失败信息
     * @param msg
     * @return
     */
    protected ResponseEntity getFailResult(String msg) {
        return new ResponseEntity("fail", RESCODE_FAIL, msg, Collections.EMPTY_MAP);
    }

    /**
     * 描述：获取默认ajax失败信息
     * @return
     */
    protected ResponseEntity getFailResult() {
        return getFailResult("操作失败");
    }

    /**
     * 描述：获取失败结果
     * @param obj
     * @param <T>
     * @return
     */
    protected <T> ResponseEntity<T> getFailResult(T obj) {
        return new ResponseEntity<>("fail", RESCODE_FAIL, "操作失败", obj);
    }

    /**
     * 描述：获取失败信息和结果
     * @param msg
     * @param errorCode
     * @param obj
     * @param <T>
     * @return
     */
    protected <T> ResponseEntity<T> getFailResult(String msg, int errorCode,T obj) {
        return new ResponseEntity<>("fail", errorCode, msg, obj);
    }

    /**
     * 描述：获取不分页的数据
     * @param list
     * @param <T>
     * @return
     */
    protected <T> CentreListResponse<T> getListResponse(List<T> list) {
        return new CentreListResponse<>(list);
    }

    /**
     * 描述：获取分页的数据
     * @param pageNum
     * @param pageSize
     * @param totalCount
     * @param list
     * @param <T>
     * @return
     */
    protected <T> CentreCutPageResponse<T> getCutPageResponse(int pageNum, int pageSize, long totalCount, List<T> list) {
        return new CentreCutPageResponse<>(pageNum, pageSize, totalCount, list);
    }

    /**
     * 描述：获取不分页的数据，BaseQueryPo是包含了页码和每页条数的Java类
     * @param <T>
     * @param baseQueryPo
     * @param totalCount
     * @param list
     * @return
     */
    protected <T> CentreCutPageResponse<T> getCutPageResponse(BaseQueryPo baseQueryPo, long totalCount, List<T> list) {
        return new CentreCutPageResponse<>(baseQueryPo.getPageNum(), baseQueryPo.getPageSize(), totalCount, list);
    }
}
