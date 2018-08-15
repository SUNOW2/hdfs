package com.software.hdfs.service;

import com.software.hdfs.domain.HdFsCondition;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 描述：
 *
 * @ClassName HdFsService
 * @Author 徐旭
 * @Date 2018/8/11 18:46
 * @Version 1.0
 */
@Service
public interface HdFsService extends BaseService<HdFsCondition, HdFsCondition> {

    /**
     * 创建编号
     *
     * @return
     */
    String createNo();

    /**
     * 批量更新
     *
     * @param list
     */
    void updateBatch(@Param("list") List<HdFsCondition> list);
}
