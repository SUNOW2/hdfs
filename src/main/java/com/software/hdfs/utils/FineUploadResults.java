package com.software.hdfs.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: ccoke
 * @Description:
 * @Date: Created in 15:23 2018/3/29
 **/
@Data
@AllArgsConstructor
public class FineUploadResults {
    private Boolean success;
    private String error;
    private Boolean reset;
    private Result result;
    public FineUploadResults(String error, Boolean reset) {
        this.error = error;
        this.reset = reset;
    }

    public FineUploadResults(Boolean success, String name, String uri) {
        this.success = success;
        this.result = new Result(name, uri);
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public class Result {
        private String name;
        private String uri;
    }
}
