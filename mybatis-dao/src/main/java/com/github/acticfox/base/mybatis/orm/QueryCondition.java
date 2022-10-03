/*
 * Copyright 2014 zhichubao.com All right reserved. This software is the
 * confidential and proprietary information of zhichubao.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with zhichubao.com .
 */
package com.github.acticfox.base.mybatis.orm;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.session.RowBounds;

import com.github.acticfox.base.mybatis.orm.filter.Filter;
import com.github.acticfox.base.mybatis.orm.filter.Sorter;

/**
 * 类 QueryCondition.java的实现描述：QueryCondition
 * 
 * @author fanyong.kfy 2014-09-10 17:14
 */
public class QueryCondition implements Map<String, Object> {
    private Filter              filter;
    private Sorter              sort;
    private RowBounds           rowBounds;
    private String              countField;

    private Map<String, Object> paramMap;

    public QueryCondition(Filter filter, Sorter sort, RowBounds rowBounds) {
        this.filter = filter;
        this.sort = sort;
        this.rowBounds = rowBounds;
    }

    public static QueryCondition of(Filter filter) {
        return of(filter, null, null);
    }

    public static QueryCondition of(Filter filter, Sorter sort) {
        return of(filter, sort, null);
    }

    public static QueryCondition of(Filter filter, Sorter sort, RowBounds rowBounds) {
        return new QueryCondition(filter, sort, rowBounds);
    }

    public String getSortSql(List<FieldDetail> columns) {
        if (sort == null) {
            return StringUtils.EMPTY;
        }
        Map<String, FieldDetail> map = new LinkedHashMap<String, FieldDetail>();
        for (FieldDetail one : columns) {
            map.put(one.getFieldName(), one);
        }

        return sort.toSortSql(map);
    }

    private Map<String, Object> getParamMap() {
        if (paramMap != null) {
            return paramMap;
        }

        paramMap = filter == null ? new LinkedHashMap<String, Object>() : filter.getParamMap();

        return paramMap;
    }

    public String getWhereSql(List<FieldDetail> columns) {
        Map<String, FieldDetail> map = new LinkedHashMap<String, FieldDetail>();
        for (FieldDetail one : columns) {
            map.put(one.getFieldName(), one);
        }

        return filter == null ? null : filter.toWhereSql(map);
    }

    public String getLimitSql() {
        return getLimitSql(rowBounds);
    }

    public static String getLimitSql(RowBounds rowBounds) {
        String limitSql = "";
        if (rowBounds != null) {
            int limit = rowBounds.getLimit();
            if (limit > 0 && limit != RowBounds.NO_ROW_LIMIT) {
                int offset = rowBounds.getOffset();
                limitSql = String.format("limit %s,%s", offset < 0 ? 0 : offset, limit);
            }
        }
        return limitSql;
    }

    public void setCountField(String countField) {
        this.countField = countField;
    }

    public String getCountField() {
        return countField;
    }

    @Override
    public int size() {
        return getParamMap().size();
    }

    @Override
    public boolean isEmpty() {
        return getParamMap().isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return getParamMap().containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return getParamMap().containsValue(value);
    }

    @Override
    public Object get(Object key) {
        return getParamMap().get(key);
    }

    @Override
    public Object put(String key, Object value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object remove(Object key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(Map<? extends String, ?> m) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Set<String> keySet() {
        return getParamMap().keySet();
    }

    @Override
    public Collection<Object> values() {
        return getParamMap().values();
    }

    @Override
    public Set<Entry<String, Object>> entrySet() {
        return getParamMap().entrySet();
    }

    public RowBounds getRowBounds() {
        return rowBounds;
    }
}
