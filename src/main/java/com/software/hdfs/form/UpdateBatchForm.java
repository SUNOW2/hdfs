package com.software.hdfs.form;

import com.software.hdfs.domain.HdFsCondition;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 描述：
 *
 * @ClassName UpdateBatchForm
 * @Author 徐旭
 * @Date 2018/8/14 10:17
 * @Version 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateBatchForm {

    /**
     * 更新的数据集合
     */
    List<HdFsCondition> list;
}
