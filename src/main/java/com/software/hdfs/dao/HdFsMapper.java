package com.software.hdfs.dao;

import com.software.hdfs.domain.HdFsCondition;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 描述：
 *
 * @ClassName HdFsMapper
 * @Author 徐旭
 * @Date 2018/8/11 18:25
 * @Version 1.0
 */
@Component
public interface HdFsMapper extends BaseMapper<HdFsCondition, HdFsCondition> {

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
    void updateBatch(List<HdFsCondition> list);

    /**
     * 批量查询
     *
     * @param list
     * @return
     */
    List<HdFsCondition> queryBatch(@Param("list") List<String> list);
}
