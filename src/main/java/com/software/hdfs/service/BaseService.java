package com.software.hdfs.service;

import java.util.List;

/**
 * @ClassName BaseService
 * @Description
 * @Author 徐旭
 * @Date 2018/7/16 10:26
 * @Version 1.0
 */
public interface BaseService<T, C> {
    /**
     * 新增记录
     * @param po
     */
    void saveRecord(T po);

    /**
     * 删除记录
     * @param id
     * @return
     */
    int deleteRecord(Object id);

    /**
     * 更新记录
     * @param po
     */
    void updateRecord(T po);

    /**
     * 查询单条记录
     * @param c
     * @return
     */
    T selectRecord(C c);

    /**
     * 获取多条记录
     * @param c
     * @return
     */
    List<T> selectListRecord(C c);

    /**
     * 获取记录的数量
     * @param po
     * @return
     */
    int countRecord(T po);

    /**
     * 分页查询
     * @param c
     * @return
     */
    List<T> selectPageListRecord(C c);
}
