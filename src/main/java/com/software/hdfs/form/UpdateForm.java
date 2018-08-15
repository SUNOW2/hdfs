package com.software.hdfs.form;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

/**
 * 描述：
 *
 * @ClassName UpdateForm
 * @Author 徐旭
 * @Date 2018/8/14 17:24
 * @Version 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateForm {

    /**
     * 文件编号
     */
    @NotBlank(message = "文件编号不能为空")
    private String hdFsNo;

    /**
     * 文件原名称
     */
    @NotBlank(message = "文件名不能为空")
    private String oldName;
}
