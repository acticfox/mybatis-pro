/*
 * Copyright 2015 zhichubao.com All right reserved. This software is the confidential and proprietary information of
 * zhichubao.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it
 * only in accordance with the terms of the license agreement you entered into with zhichubao.com .
 */
package com.github.acticfox.base.mybatis.orm.dao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.InitializingBean;

import com.github.acticfox.base.dao.QueryDAO;
import com.github.acticfox.base.dao.SimpleDao;
import com.github.acticfox.base.dao.SqlHolder;
import com.github.acticfox.base.dao.UpdateDAO;
import com.github.acticfox.base.mybatis.orm.EntityManager;
import com.github.acticfox.base.mybatis.orm.ReflectionUtils;
import com.github.acticfox.base.mybatis.orm.filter.Filter;
import com.github.acticfox.base.mybatis.orm.filter.Sorter;
import com.github.acticfox.common.api.result.PageResult;
import com.github.acticfox.common.api.result.ResultList;
import com.google.common.collect.Lists;

/**
 * 类BaseDao.java的实现描述：
 * 
 * <pre>
 * </pre>
 * 
 * @author fanyong.kfy 2015年4月12日 下午5:57:16
 */
public abstract class BaseDao<T extends Serializable> implements SimpleDao<T>, InitializingBean {
    private static final int BATCH_LIMIT = 500;

    @Resource
    private EntityManager entityManager;

    private Class<T> clazz;

    @Resource
    protected QueryDAO queryDAO;

    @Resource
    protected UpdateDAO updateDAO;

    @Override
    public void afterPropertiesSet() throws Exception {
        this.clazz = ReflectionUtils.getSuperClassGenricType(getClass());
        entityManager.init(clazz);
    }

    /**
     * 根据ID查询对象
     * 
     * @param id 对象ID
     * @return 对象
     */
    @Override
    public T queryById(Serializable id) {
        return entityManager.queryById(clazz, id);
    }

    /**
     * 插入数据
     * 
     * @param entity 数据信息
     * @return 影响行数
     */
    @Override
    public int insert(T entity) {
        return entityManager.insert(clazz, entity);
    }

    /**
     * 更新对象
     * 
     * @param entity 数据信息
     * @return 影响行数
     */
    @Override
    public int update(T entity) {
        return entityManager.update(clazz, entity);
    }

    /**
     * 新增或修改对象
     * 
     * @param entity 对象
     * @return 影响行数
     */
    @Override
    public int saveOrUpdate(T entity) {
        return entityManager.saveOrUpdate(clazz, entity);
    }

    /**
     * 删除对象
     * 
     * @param id 对象ID
     * @return 影响行数
     */
    @Override
    public int delete(Serializable id) {
        return entityManager.delete(clazz, id);
    }

    /**
     * 分页查询
     * 
     * @param sqlId 主查询SqlID，CountSql的ID为 主查询SqlID + "Count"
     * @param bindParams 参数
     * @param pageNum 当前要查询的页数
     * @param pageSize 每页数据的行数
     * @return 分页对象
     */
    @Override
    public <E> PageResult<E> queryPage(String sqlId, Object bindParams, int pageNum, int pageSize) {
        int count = queryDAO.executeForObject(sqlId + "Count", bindParams, Integer.class);
        PageResult<E> pageResult = new PageResult<E>(count, pageNum, pageSize);
        List<E> dataList = queryDAO.executeForObjectList(sqlId, bindParams, pageResult.getStartPosition(), pageSize);
        pageResult.bindData(dataList);

        return pageResult;
    }

    /**
     * Count
     * 
     * @param paramMap 参数
     * @return Count
     */
    protected Integer count(Map<String, Object> paramMap) {
        return entityManager.count(clazz, paramMap);
    }

    /**
     * 更新字段，只更新非NULL字段
     * 
     * @param entity entity
     * @return 更新行数
     */
    public int updateNoneNullField(T entity) {
        return entityManager.updateNoneNullField(clazz, entity);
    }

    /**
     * 更新字段，只更新非NULL字段
     * 
     * @param entity entity
     * @return 更新行数
     */
    protected int updateNoneNullField(T entity, Filter filter) {
        return entityManager.updateNoneNullField(clazz, entity, filter);
    }

    /**
     * 查询列表
     * 
     * @param queryNode 查询条件
     * @param sortNodes 排序条件
     * @return 列表
     */
    protected List<T> queryList(Filter filter, Sorter sort) {
        return entityManager.queryList(clazz, filter, sort);
    }

    /**
     * 查询列表
     *
     * @param queryNode 查询条件
     * @param sortNodes 排序条件
     * @return 列表
     */
    protected List<T> queryList(Filter filter, Sorter sort, int start, int limit) {
        return entityManager.queryList(clazz, filter, sort, start, limit);
    }

    /**
     * 查询列表
     * 
     * @param queryNode 查询条件
     * @return 列表
     */
    protected List<T> queryList(Filter filter) {
        return entityManager.queryList(clazz, filter, null);
    }

    /**
     * 查询单条记录
     * 
     * @param queryNode 参数
     * @return 对象
     */
    protected T queryOne(Filter filter) {
        return entityManager.queryOne(clazz, filter);
    }

    /**
     * Count
     * 
     * @param queryNode 参数
     * @return Count
     */
    protected Integer count(Filter filter) {
        return entityManager.count(clazz, filter);
    }

    /**
     * 查询列表
     * 
     * @param filter 查询条件
     * @param offset 偏移
     * @param length 条数
     * @return 列表
     */
    protected ResultList<T> queryResultList(Filter filter, int offset, int length) {
        int count = count(filter);
        List<T> resultList = entityManager.queryList(clazz, filter, null, offset, length);

        return new ResultList<T>(count, resultList);
    }

    /**
     * 查询列表
     * 
     * @param filter 查询条件
     * @param sort 排序条件
     * @param offset 偏移
     * @param length 条数
     * @return 列表
     */
    protected ResultList<T> queryResultList(Filter filter, Sorter sort, int offset, int length) {
        int count = count(filter);
        List<T> resultList = entityManager.queryList(clazz, filter, sort, offset, length);

        return new ResultList<T>(count, resultList);
    }

    /**
     * 分页查询
     * 
     * @param filter 查询参数
     * @param pageNum 当前要查询的页数
     * @param pageSize 每页数据的行数
     * @return 分页对象
     */
    protected PageResult<T> queryPage(Filter filter, int pageNum, int pageSize) {
        return entityManager.queryPageResult(clazz, filter, null, pageNum, pageSize);
    }

    /**
     * 分页查询
     * 
     * @param filter 查询参数
     * @param sort 排序参数
     * @param pageNum 当前要查询的页数
     * @param pageSize 每页数据的行数
     * @return 分页对象
     */
    protected PageResult<T> queryPage(Filter filter, Sorter sort, int pageNum, int pageSize) {
        return entityManager.queryPageResult(clazz, filter, sort, pageNum, pageSize);
    }

    /**
     * 分页查询
     *
     * @param filter 查询参数
     * @param sort 排序参数
     * @param pageNum 当前要查询的页数
     * @param pageSize 每页数据的行数
     * @param countField Count(xx)中的字段名
     * @return 分页对象
     */
    protected PageResult<T> queryPage(Filter filter, Sorter sort, int pageNum, int pageSize, String countField) {
        return entityManager.queryPageResult(clazz, filter, sort, pageNum, pageSize, countField);
    }

    @Override
    public void batchUpdateExecute(List<T> objectList, boolean isInsert) {
        if (CollectionUtils.isEmpty(objectList)) {
            return;
        }

        String sqlId = "";
        if (isInsert) {
            sqlId = entityManager.getInsertSqlConfig(clazz);
        } else {
            sqlId = entityManager.getUpdateSqlConfig(clazz);
        }

        List<List<T>> partition = Lists.partition(objectList, BATCH_LIMIT);
        for (List<T> objects : partition) {
            List<SqlHolder> sqlHolderList = new ArrayList<SqlHolder>(objects.size());
            for (T t : objects) {
                SqlHolder sqlHolder = new SqlHolder(sqlId, t);
                sqlHolderList.add(sqlHolder);
            }

            updateDAO.executeBatch(sqlHolderList);
        }
    }
}
