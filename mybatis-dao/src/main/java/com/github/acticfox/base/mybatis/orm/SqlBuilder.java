/*
 * Copyright 2014 github.com All right reserved. This software is the
 *    confidential and proprietary information of github.com ("Confidential
 *    Information"). You shall not disclose such Confidential Information and shall
 *    use it only in accordance with the terms of the license agreement you entered
 *    into with github.com .
 */

package com.github.acticfox.base.mybatis.orm;

import java.lang.reflect.Constructor;
import java.util.List;
import java.util.Map;

import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.jdbc.SQL;
import org.apache.ibatis.mapping.ResultMapping;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.type.TypeHandler;
import org.springframework.beans.BeanUtils;

/**
 * 类 SqlBuilder.java 的实现描述：TODO 类实现描述
 * 
 * @author fanyong.kfy 14-1-17 上午10:43
 */
public class SqlBuilder {
    public static final String ORDER_KEY                = "__order_key__";
    public static final String LIMIT_KEY                = "__limit_key__";
    public static final String COUNT_FILED_KEY                = "__count_filed_key__";
    private static final String COUNT_FILED_DEFAULT_VALUE = "1";

    public static final String DEFAULT_PRIMARY_KEY_NAME = "id";

    private TableHandler       tableHandler;
    private FieldDetail        primaryKeyField;

    public SqlBuilder(Class<?> targetClass) {
        this.tableHandler = new TableHandler(targetClass);

        // 读取注解指定的主键列
        for (FieldDetail field : tableHandler.getColumns()) {
            if (field.isPk()) {
                this.primaryKeyField = field;
                break;
            }
        }
    }

    public String getTableName() {
        return tableHandler.getTableName();
    }

    public String getPrimaryKeyFieldName() {
        return primaryKeyField == null ? DEFAULT_PRIMARY_KEY_NAME : primaryKeyField.getFieldName();
    }

    public String getPrimaryKeyColumnName() {
        return primaryKeyField == null ? DEFAULT_PRIMARY_KEY_NAME : primaryKeyField.getColumnName();
    }

    /**
     * 生成Insert sql
     * 
     * @return
     */
    public String getInsertSql() {
        final SQL sql = new SQL();
        sql.INSERT_INTO(tableHandler.getTableName());

        for (FieldDetail column : tableHandler.getColumns()) {
            if (column.isInsertAble()) {
                sql.VALUES("`" + column.getColumnName() + "`", column.getFieldNameWithWrap());
            }

        }
        return sql.toString();
    }

    /**
     * 生成update sql
     * 
     * @param parameterObject 要更新的实体
     * @return
     */
    public String getUpdateSql(Object parameterObject) {
        SQL sql = new SQL();
        sql.UPDATE(tableHandler.getTableName());

        boolean isMap = parameterObject instanceof Map;
        for (FieldDetail column : tableHandler.getColumns()) {
            if (column.isUpdateAble()) {
                if (isMap) {
                    if (null != parameterObject && ((Map) parameterObject).containsKey(column.getFieldName())) {
                        sql.SET("`" + column.getColumnName() + "`=" + column.getFieldNameWithWrap());
                    }
                } else {
                    sql.SET("`" + column.getColumnName() + "`=" + column.getFieldNameWithWrap());
                }
            }
        }
        sql.WHERE(String.format("`%s`=#{%s}", getPrimaryKeyColumnName(), getPrimaryKeyFieldName()));
        return sql.toString();
    }

    /**
     * 生成update sql
     * 
     * @param updateCondition 要更新的实体
     * @return SQL
     */
    public String getUpdateSql(UpdateCondition updateCondition) {
        SQL sql = new SQL();
        sql.UPDATE(tableHandler.getTableName());

        Map<String, Object> updateFields = updateCondition.getUpdateFields();
        for (FieldDetail column : tableHandler.getColumns()) {
            if (column.isUpdateAble() && updateFields.containsKey(column.getFieldName())) {
                sql.SET("`" + column.getColumnName() + "`=" + column.getFieldNameWithWrap());
            }
        }

        // where
        sql.WHERE(String.format("`%s`=#{%s}", getPrimaryKeyColumnName(), getPrimaryKeyFieldName()));
        String whereSql = updateCondition.getWhereSql(tableHandler.getColumns());
        if (StringUtils.isNotBlank(whereSql)) {
            sql.WHERE(whereSql);
        }

        return sql.toString();
    }

    /**
     * 删除 by ID
     * 
     * @return
     */
    public String getDeleteSql() {
        SQL sql = new SQL();
        sql.DELETE_FROM(tableHandler.getTableName()).WHERE(
                String.format("`%s`=#{%s}", getPrimaryKeyColumnName(), getPrimaryKeyFieldName()));
        return sql.toString();
    }

    /**
     * count sql
     * 
     * @param condition
     * @return
     */
    public String getCountSql(Map<String, Object> condition) {
        SQL sql = new SQL();
        Object countFiledObj = condition.get(COUNT_FILED_KEY);
        String countFiled = countFiledObj == null ? null : String.valueOf(countFiledObj);
        if (StringUtils.isBlank(countFiled)) {
            countFiled = COUNT_FILED_DEFAULT_VALUE;
        }
        sql.SELECT("count(" + countFiled + ")").FROM(getTableName());
        appendUpdate(condition, sql);
        return sql.toString();
    }

    public String getCountSql(QueryCondition parameterObject) {
        SQL sql = new SQL();
        String countFiled = parameterObject.getCountField();
        if (StringUtils.isBlank(countFiled)) {
            countFiled = COUNT_FILED_DEFAULT_VALUE;
        }
        sql.SELECT("count(" + countFiled + ")").FROM(getTableName());

        // where
        String whereSql = parameterObject.getWhereSql(tableHandler.getColumns());
        if (StringUtils.isNotBlank(whereSql)) {
            sql.WHERE(whereSql);
        }

        return sql.toString();
    }

    /**
     * select by condition
     * 
     * @param condition
     * @return
     */
    public String getSelectSql(Map<String, Object> condition) {
        SQL sql = new SQL();
        sql.SELECT("*").FROM(tableHandler.getTableName());
        appendUpdate(condition, sql);
        if (condition.containsKey(ORDER_KEY)) {
            sql.ORDER_BY(condition.get(ORDER_KEY).toString());
        }

        String sqlStr = sql.toString();

        // limit
        String limitSql = "";
        Object rowBound = condition.get(LIMIT_KEY);
        if (rowBound != null && RowBounds.class.isInstance(rowBound)) {
            limitSql = QueryCondition.getLimitSql((RowBounds) rowBound);
        }
        if (StringUtils.isNotBlank(limitSql)) {
            sqlStr += " " + limitSql;
        }

        return sqlStr;
    }

    public String getSelectSql(QueryCondition parameterObject) {
        SQL sql = new SQL();
        sql.SELECT("*").FROM(tableHandler.getTableName());

        // where
        String whereSql = parameterObject.getWhereSql(tableHandler.getColumns());
        if (StringUtils.isNotBlank(whereSql)) {
            sql.WHERE(whereSql);
        }

        // sort
        String sortSql = parameterObject.getSortSql(tableHandler.getColumns());
        if (StringUtils.isNotBlank(sortSql)) {
            sql.ORDER_BY(sortSql);
        }

        String sqlStr = sql.toString();

        // limit
        String limitSql = parameterObject.getLimitSql();
        if (StringUtils.isNotBlank(limitSql)) {
            sqlStr += " " + limitSql;
        }

        return sqlStr;
    }

    private void appendUpdate(Map<String, Object> param, SQL sql) {
        if (param == null || param.isEmpty()) {
            return;
        }
        for (FieldDetail column : tableHandler.getColumns()) {
            //modify by haizheng.pang 2014 07 31
            if (param.get(column.getFieldName()) != null) {
                //            if (param.containsKey(column.getFieldName())) {
                sql.WHERE("`" + column.getColumnName() + "`=" + column.getFieldNameWithWrap());
            }
        }
    }

    public List<ResultMapping> getResultMappingList(final Configuration configuration) {
        return FluentIterable.from(tableHandler.getColumns()).transform(new Function<FieldDetail, ResultMapping>() {
            @Override
            public ResultMapping apply(FieldDetail column) {
                ResultMapping.Builder builder = new ResultMapping.Builder(configuration, column.getFieldName(), column
                        .getColumnName(), column.getField().getType());
                Class<? extends TypeHandler> typeHandler = column.getTypeHandler();
                if (typeHandler != null) {
                    try {
                        Constructor constructor = typeHandler.getConstructor(column.getField().getType().getClass());
                        builder.typeHandler((org.apache.ibatis.type.TypeHandler<?>) BeanUtils.instantiateClass(
                                constructor, column.getField().getType()));
                    } catch (NoSuchMethodException ignore) {
                    }
                }
                return builder.build();
            }
        }).toList();
    }
}
