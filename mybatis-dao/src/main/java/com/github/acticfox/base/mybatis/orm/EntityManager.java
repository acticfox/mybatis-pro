/*
 * Copyright 2014 github.com All right reserved. This software is the confidential and proprietary information of
 * github.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it
 * only in accordance with the terms of the license agreement you entered into with github.com .
 */
package com.github.acticfox.base.mybatis.orm;

import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.builder.SqlSourceBuilder;
import org.apache.ibatis.cache.Cache;
import org.apache.ibatis.executor.keygen.Jdbc3KeyGenerator;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.mapping.ResultMapping;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionTemplate;

import com.github.acticfox.base.mybatis.orm.filter.Filter;
import com.github.acticfox.base.mybatis.orm.filter.Sorter;
import com.github.acticfox.common.api.annotation.CacheType;
import com.github.acticfox.common.api.result.PageResult;
import com.github.acticfox.mybatis.plugin.cache.memcached.LoggingMemcachedCache;
import com.github.acticfox.mybatis.plugin.cache.redis.LoggingRedisCache;
import com.google.common.base.Throwables;

/**
 * 类EntityManager.java的实现描述：MyBatisOrm封装类
 * 
 * @author fanyong.kfy 14-1-17 15:08
 */
@SuppressWarnings("unchecked")
public class EntityManager {

    private SqlSession sqlSessionTemplate;
    private SqlSourceBuilder sqlSourceBuilder;
    private String primaryKeyFieldName = SqlBuilder.DEFAULT_PRIMARY_KEY_NAME;
    private String createTimeColumnName = "createdTime";
    private String updateTimeColumnName = "updatedTime";

    private static Map<Class<?>, SqlIdConfig> alreadyInitClass = new ConcurrentHashMap<Class<?>, SqlIdConfig>();

    public void setSqlSessionFactory(SqlSessionFactory sqlSessionFactory) {
        this.sqlSessionTemplate = new SqlSessionTemplate(sqlSessionFactory);
        this.sqlSourceBuilder = new SqlSourceBuilder(sqlSessionFactory.getConfiguration());
    }

    public void setCreateTimeColumnName(String createTimeColumnName) {
        createTimeColumnName = StringUtils.trimToEmpty(createTimeColumnName);
        if (StringUtils.isNotBlank(createTimeColumnName)) {
            this.createTimeColumnName = createTimeColumnName;
        }
    }

    public void setUpdateTimeColumnName(String updateTimeColumnName) {
        updateTimeColumnName = StringUtils.trimToEmpty(updateTimeColumnName);
        if (StringUtils.isNotBlank(updateTimeColumnName)) {
            this.updateTimeColumnName = updateTimeColumnName;
        }
    }

    /**
     * 根据ID查询对象
     * 
     * @param id 对象ID
     * @return 对象
     */
    public <T> T queryById(Class<T> clazz, Serializable id) {
        if (id == null) {
            return null;
        }

        Map<String, Object> param = new HashMap<String, Object>();
        param.put(primaryKeyFieldName, id);
        return queryOne(clazz, param);
    }

    /**
     * 查询单条记录
     * 
     * @param clazz 对象烈香
     * @param param 参数
     * @return 对象
     */
    public <T> T queryOne(Class<T> clazz, Map<String, ? extends Object> param) {
        SqlIdConfig sqlIdConfig = alreadyInitClass.get(clazz);
        return (T)sqlSessionTemplate.selectOne(sqlIdConfig.selectSqlId, param);
    }

    /**
     * 插入数据
     * 
     * @param entity 数据信息
     * @return 影响行数
     */
    public <T> int insert(Class<T> clazz, T entity) {
        SqlIdConfig sqlConfig = alreadyInitClass.get(clazz);

        if (ReflectionUtils.containsField(entity, createTimeColumnName)) {
            ReflectionUtils.setFieldValue(entity, createTimeColumnName, new Date());
        }
        if (ReflectionUtils.containsField(entity, updateTimeColumnName)) {
            ReflectionUtils.setFieldValue(entity, updateTimeColumnName, new Date());
        }

        return sqlSessionTemplate.insert(sqlConfig.insertSqlId, entity);
    }

    /**
     * 更新对象
     * 
     * @param entity 数据信息
     * @return 影响行数
     */
    public <T> int update(Class<T> clazz, T entity) {
        SqlIdConfig sqlConfig = alreadyInitClass.get(clazz);

        if (ReflectionUtils.containsField(entity, updateTimeColumnName)) {
            ReflectionUtils.setFieldValue(entity, updateTimeColumnName, new Date());
        }

        return sqlSessionTemplate.update(sqlConfig.updateSqlId, entity);
    }

    public <T> int updateNoneNullField(Class<T> clazz, T entity) {
        SqlIdConfig sqlConfig = alreadyInitClass.get(clazz);

        if (ReflectionUtils.containsField(entity, updateTimeColumnName)) {
            ReflectionUtils.setFieldValue(entity, updateTimeColumnName, new Date());
        }

        return sqlSessionTemplate.update(sqlConfig.updateSqlId, BeanUtil.describe(entity));
    }

    public <T> int updateNoneNullField(Class<T> clazz, T entity, Filter filter) {
        SqlIdConfig sqlConfig = alreadyInitClass.get(clazz);

        if (ReflectionUtils.containsField(entity, updateTimeColumnName)) {
            ReflectionUtils.setFieldValue(entity, updateTimeColumnName, new Date());
        }

        return sqlSessionTemplate.update(sqlConfig.updateByNodeSqlId,
            UpdateCondition.of(BeanUtil.describe(entity), filter));
    }

    public <T> int saveOrUpdate(Class<T> clazz, T entity) {
        Object pkId = null;
        try {
            pkId = ReflectionUtils.getFieldValue(entity, primaryKeyFieldName);
        } catch (Exception e) {
            throw Throwables.propagate(e);
        }

        if (pkId == null) {
            return insert(clazz, entity);
        } else {
            return update(clazz, entity);
        }
    }

    public <T> Integer count(Class<T> clazz, Map<String, Object> paramMap) {
        paramMap = paramMap == null ? new HashMap<String, Object>() : new HashMap<String, Object>(paramMap);

        SqlIdConfig sqlIdConfig = alreadyInitClass.get(clazz);

        return sqlSessionTemplate.selectOne(sqlIdConfig.countSqlId, paramMap);
    }

    public <T> List<T> queryList(Class<T> clazz, Map<String, Object> paramMap, String orderBy) {
        paramMap = paramMap == null ? new HashMap<String, Object>() : new HashMap<String, Object>(paramMap);
        if (StringUtils.isNotEmpty(orderBy)) {
            paramMap.put(SqlBuilder.ORDER_KEY, orderBy);
        }

        SqlIdConfig sqlIdConfig = alreadyInitClass.get(clazz);

        return (List<T>)sqlSessionTemplate.selectList(sqlIdConfig.selectSqlId, paramMap);
    }

    public <T> List<T> queryList(Class<T> clazz, Map<String, Object> paramMap, String orderBy, int offset, int length) {
        paramMap = paramMap == null ? new HashMap<String, Object>() : new HashMap<String, Object>(paramMap);
        if (StringUtils.isNotEmpty(orderBy)) {
            paramMap.put(SqlBuilder.ORDER_KEY, orderBy);
        }
        paramMap.put(SqlBuilder.LIMIT_KEY, new RowBounds(offset, length));

        SqlIdConfig sqlIdConfig = alreadyInitClass.get(clazz);
        return sqlSessionTemplate.selectList(sqlIdConfig.selectSqlId, paramMap);
    }

    public <T> T queryOne(Class<T> clazz, Filter filter) {
        SqlIdConfig sqlIdConfig = alreadyInitClass.get(clazz);

        return (T)sqlSessionTemplate.selectOne(sqlIdConfig.selectByNodeSqlId, QueryCondition.of(filter));
    }

    public <T> List<T> queryList(Class<T> clazz, Filter filter, Sorter sort) {
        SqlIdConfig sqlIdConfig = alreadyInitClass.get(clazz);

        return (List<T>)sqlSessionTemplate.selectList(sqlIdConfig.selectByNodeSqlId, QueryCondition.of(filter, sort));
    }

    public <T> Integer count(Class<T> clazz, Filter filter) {
        return count(clazz, filter, null);
    }

    public <T> Integer count(Class<T> clazz, Filter filter, String countField) {
        SqlIdConfig sqlIdConfig = alreadyInitClass.get(clazz);

        QueryCondition condition = QueryCondition.of(filter);
        condition.setCountField(countField);
        return sqlSessionTemplate.selectOne(sqlIdConfig.countByNodeSqlId, condition);
    }

    public <T> List<T> queryList(Class<T> clazz, Filter filter, Sorter sort, int offset, int length) {
        SqlIdConfig sqlIdConfig = alreadyInitClass.get(clazz);
        return sqlSessionTemplate.selectList(sqlIdConfig.selectByNodeSqlId,
            QueryCondition.of(filter, sort, new RowBounds(offset, length)));
    }

    public <T> PageResult<T> queryPageResult(Class<T> clazz, Filter filter, Sorter sort, int pageNum, int pageSize) {
        return queryPageResult(clazz, filter, sort, pageNum, pageSize, null);
    }

    public <T> PageResult<T> queryPageResult(Class<T> clazz, Filter filter, Sorter sort, int pageNum, int pageSize,
        String countField) {
        Integer count = count(clazz, filter, countField);
        PageResult<T> pageResult = new PageResult<T>(count, pageNum, pageSize);

        int offset = pageNum > 0 ? (pageNum - 1) * pageSize : 0;
        pageResult.bindData(queryList(clazz, filter, sort, offset, pageSize));

        return pageResult;
    }

    public <T> PageResult<T> queryPageResult(Class<T> clazz, Map<String, Object> paramMap, String orderBy, int pageNum,
        int pageSize) {
        paramMap = paramMap == null ? new HashMap<String, Object>() : new HashMap<String, Object>(paramMap);
        if (StringUtils.isNotEmpty(orderBy)) {
            paramMap.put(SqlBuilder.ORDER_KEY, orderBy);
        }

        Integer count = count(clazz, paramMap);
        PageResult<T> pageResult = new PageResult<T>(count, pageNum, pageSize);

        int offset = pageNum > 0 ? (pageNum - 1) * pageSize : 0;
        pageResult.bindData(queryList(clazz, paramMap, orderBy, offset, pageSize));

        return pageResult;
    }

    /**
     * 删除对象
     * 
     * @param id 对象ID
     * @return 影响行数
     */
    public <T> int delete(Class<T> clazz, Serializable id) {
        SqlIdConfig sqlConfig = alreadyInitClass.get(clazz);

        return sqlSessionTemplate.delete(sqlConfig.deleteSqlId, id);
    }

    /**
     * 获取Object对应的常规的sqlId
     * 
     * @param clazz
     * @param <T>
     * @return
     */
    public <T> String getUpdateSqlConfig(Class<T> clazz) {
        return alreadyInitClass.get(clazz).updateSqlId;
    }

    public <T> String getInsertSqlConfig(Class<T> clazz) {
        return alreadyInitClass.get(clazz).insertSqlId;
    }

    public synchronized void init(final Class<?> clazz) {
        if (alreadyInitClass.containsKey(clazz)) {
            return;
        }
        SqlIdConfig sqlConfig = new SqlIdConfig();

        Configuration configuration = sqlSessionTemplate.getConfiguration();
        final SqlBuilder sqlBuilder = new SqlBuilder(clazz);
        this.primaryKeyFieldName = sqlBuilder.getPrimaryKeyFieldName();

        // 缓存配置
        Cache cache = null;
        CacheType cacheAnnotation = clazz.getAnnotation(CacheType.class);
        if (cacheAnnotation != null) {
            switch (cacheAnnotation.value()) {
                case LRU_Memcached:
                    cache = new LoggingMemcachedCache(clazz.getName());
                    break;
                case LRU_Redis:
                    cache = new LoggingRedisCache(clazz.getName());
                    break;
            }
        }
        if (cache != null) {
            configuration.addCache(cache);
        }

        // Insert
        SqlSource insertSqlSource = new SqlSource() {
            @Override
            public BoundSql getBoundSql(Object parameterObject) {
                return sqlSourceBuilder.parse(sqlBuilder.getInsertSql(), clazz, null).getBoundSql(parameterObject);
            }
        };
        sqlConfig.insertSqlId = String.format("%s_AutoGen_%s", sqlBuilder.getTableName(), "Insert");
        addInto(sqlSessionTemplate, sqlConfig.insertSqlId, insertSqlSource, SqlCommandType.INSERT, clazz, null, cache);

        // Update
        SqlSource updateSqlSource = new SqlSource() {
            @Override
            public BoundSql getBoundSql(Object parameterObject) {
                return sqlSourceBuilder.parse(sqlBuilder.getUpdateSql(parameterObject), clazz, null)
                    .getBoundSql(parameterObject);
            }
        };
        sqlConfig.updateSqlId = String.format("%s_AutoGen_%s", sqlBuilder.getTableName(), "Update");
        addInto(sqlSessionTemplate, sqlConfig.updateSqlId, updateSqlSource, SqlCommandType.UPDATE, null, null, cache);

        // UpdateByNode
        SqlSource updateByNodeSqlSource = new SqlSource() {
            @Override
            public BoundSql getBoundSql(Object parameterObject) {
                return sqlSourceBuilder.parse(sqlBuilder.getUpdateSql((UpdateCondition)parameterObject), clazz, null)
                    .getBoundSql(parameterObject);
            }
        };
        sqlConfig.updateByNodeSqlId = String.format("%s_AutoGen_%s", sqlBuilder.getTableName(), "UpdateByNode");
        addInto(sqlSessionTemplate, sqlConfig.updateByNodeSqlId, updateByNodeSqlSource, SqlCommandType.UPDATE, null,
            null, cache);

        // Delete
        SqlSource deleteSqlSource = new SqlSource() {
            @Override
            public BoundSql getBoundSql(Object parameterObject) {
                return sqlSourceBuilder.parse(sqlBuilder.getDeleteSql(), clazz, null).getBoundSql(parameterObject);
            }
        };
        sqlConfig.deleteSqlId = String.format("%s_AutoGen_%s", sqlBuilder.getTableName(), "Delete");
        addInto(sqlSessionTemplate, sqlConfig.deleteSqlId, deleteSqlSource, SqlCommandType.DELETE, null, null, cache);

        // Select
        SqlSource selectSqlSource = new SqlSource() {
            @Override
            public BoundSql getBoundSql(Object parameterObject) {
                return sqlSourceBuilder
                    .parse(sqlBuilder.getSelectSql((Map<String, Object>)parameterObject), clazz, null)
                    .getBoundSql(parameterObject);
            }
        };
        sqlConfig.selectSqlId = String.format("%s_AutoGen_%s", sqlBuilder.getTableName(), "Select");
        addInto(sqlSessionTemplate, sqlConfig.selectSqlId, selectSqlSource, SqlCommandType.SELECT, clazz,
            sqlBuilder.getResultMappingList(configuration), cache);

        // Count
        SqlSource countSqlSource = new SqlSource() {
            @Override
            public BoundSql getBoundSql(Object parameterObject) {
                return sqlSourceBuilder.parse(sqlBuilder.getCountSql((Map<String, Object>)parameterObject), clazz, null)
                    .getBoundSql(parameterObject);
            }
        };
        sqlConfig.countSqlId = String.format("%s_AutoGen_%s", sqlBuilder.getTableName(), "Count");
        addInto(sqlSessionTemplate, sqlConfig.countSqlId, countSqlSource, SqlCommandType.SELECT, Integer.class,
            Collections.<ResultMapping>emptyList(), cache);

        // SelectByNode
        SqlSource selectByNodeSqlSource = new SqlSource() {
            @Override
            public BoundSql getBoundSql(Object parameterObject) {
                return sqlSourceBuilder.parse(sqlBuilder.getSelectSql((QueryCondition)parameterObject), clazz, null)
                    .getBoundSql(parameterObject);
            }
        };
        sqlConfig.selectByNodeSqlId = String.format("%s_AutoGen_%s", sqlBuilder.getTableName(), "SelectByNode");
        addInto(sqlSessionTemplate, sqlConfig.selectByNodeSqlId, selectByNodeSqlSource, SqlCommandType.SELECT, clazz,
            sqlBuilder.getResultMappingList(configuration), cache);

        // Count
        SqlSource countByNodeSqlSource = new SqlSource() {
            @Override
            public BoundSql getBoundSql(Object parameterObject) {
                return sqlSourceBuilder.parse(sqlBuilder.getCountSql((QueryCondition)parameterObject), clazz, null)
                    .getBoundSql(parameterObject);
            }
        };
        sqlConfig.countByNodeSqlId = String.format("%s_AutoGen_%s", sqlBuilder.getTableName(), "CountByNode");
        addInto(sqlSessionTemplate, sqlConfig.countByNodeSqlId, countByNodeSqlSource, SqlCommandType.SELECT,
            Integer.class, Collections.<ResultMapping>emptyList(), cache);

        alreadyInitClass.put(clazz, sqlConfig);
    }

    private void addInto(SqlSession sqlSessionTemplate, String sqlId, SqlSource sqlSource,
        SqlCommandType sqlCommandType, Class<?> resultType, List<ResultMapping> resultMappingList, Cache cache) {
        // 获取配置信息
        Configuration configuration = sqlSessionTemplate.getConfiguration();

        // 创建StatementBuilder
        MappedStatement.Builder statementBuilder =
            new MappedStatement.Builder(configuration, sqlId, sqlSource, sqlCommandType);

        // 缓存配置
        boolean isSelect = sqlCommandType == SqlCommandType.SELECT;
        statementBuilder.flushCacheRequired(!isSelect);
        statementBuilder.useCache(configuration.isCacheEnabled() && isSelect);
        if (cache != null) {
            statementBuilder.cache(cache);
            statementBuilder.useCache(isSelect);
        }

        // 设置超时
        statementBuilder.timeout(configuration.getDefaultStatementTimeout());

        // 返回值与对象的Mapping
        if (sqlCommandType == SqlCommandType.SELECT && resultType != null) {
            List<ResultMap> resultMaps = new ArrayList<ResultMap>();
            ResultMap.Builder resultMapBuilder =
                new ResultMap.Builder(configuration, statementBuilder.id() + "-Inline", resultType, resultMappingList);
            resultMaps.add(resultMapBuilder.build());
            statementBuilder.resultMaps(resultMaps);
        }

        // 主键生成配置
        if (SqlCommandType.INSERT == sqlCommandType) {
            GeneratedValue generatedValue = resultType.getAnnotation(GeneratedValue.class);
            if (generatedValue == null || generatedValue.strategy() == GenerationType.IDENTITY) {
                statementBuilder.keyProperty(primaryKeyFieldName);
                statementBuilder.keyGenerator(new Jdbc3KeyGenerator());
            }
        }

        // 放入Mybatis
        configuration.addMappedStatement(statementBuilder.build());
    }

    private static class BeanUtil {
        public static Map<String, Object> describe(Object obj) {
            Map<String, Object> map = new HashMap<String, Object>();
            if (null == obj) {
                return map;
            }

            PropertyDescriptor[] descriptors = PropertyUtils.getPropertyDescriptors(obj);
            for (PropertyDescriptor one : descriptors) {
                if (one.getPropertyType().equals(Class.class)) {
                    continue;
                }

                String name = one.getName();
                if (one.getReadMethod() != null) {
                    Object value = null;
                    try {
                        value = one.getReadMethod().invoke(obj);
                    } catch (Exception e) {
                        Throwables.propagate(e);
                    }

                    if (value != null) {
                        map.put(name, value);
                    }
                }
            }

            return map;
        }
    }

    private class SqlIdConfig {
        String insertSqlId;
        String updateSqlId;
        String deleteSqlId;
        String selectSqlId;
        String countSqlId;
        String selectByNodeSqlId;
        String countByNodeSqlId;
        String updateByNodeSqlId;
    }
}
