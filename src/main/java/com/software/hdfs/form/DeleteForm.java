package com.software.hdfs.form;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

/**
 * 描述：
 *
 * @ClassName DeleteForm
 * @Author 徐旭
 * @Date 2018/8/14 09:38
 * @Version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeleteForm {

    /**
     * 文件编号
     */
    @NotBlank(message = "文件编号不可以为空")
    private String hdFsNo;

    /**
     * 文件路径
     */
    @NotEmpty(message = "文件路径不能为空")
    private String filePath;
}
