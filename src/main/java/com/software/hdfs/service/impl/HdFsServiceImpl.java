package com.software.hdfs.service.impl;

import com.software.hdfs.dao.HdFsMapper;
import com.software.hdfs.domain.HdFsCondition;
import com.software.hdfs.service.HdFsService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 描述：
 *
 * @ClassName HdFsServiceImpl
 * @Author 徐旭
 * @Date 2018/8/11 18:47
 * @Version 1.0
 */
@Service
public class HdFsServiceImpl extends BaseServiceImpl<HdFsCondition, HdFsCondition, HdFsMapper> implements HdFsService {

    @Override
    public String createNo() {
        return this.getMapper().createNo();
    }

    @Override
    public void updateBatch(List<HdFsCondition> list) {
        this.getMapper().updateBatch(list);
    }
}
