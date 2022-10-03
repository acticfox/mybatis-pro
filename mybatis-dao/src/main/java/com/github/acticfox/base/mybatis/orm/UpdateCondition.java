/*
 * Copyright 2014 zhichubao.com All right reserved. This software is the
 * confidential and proprietary information of zhichubao.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with zhichubao.com .
 */
package com.github.acticfox.base.mybatis.orm;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.github.acticfox.base.mybatis.orm.filter.Filter;

/**
 * 类 QueryCondition.java的实现描述：QueryCondition
 * 
 * @author fanyong.kfy 2014-09-10 17:14
 */
public class UpdateCondition implements Map<String, Object> {
    private Filter              filter;
    private Map<String, Object> updateFields;
    private Map<String, Object> paramMap;

    public UpdateCondition(Map<String, Object> updateFields, Filter filter) {
        this.filter = filter;
        this.updateFields = updateFields;
    }

    public static UpdateCondition of(Map<String, Object> updateFields, Filter filter) {
        return new UpdateCondition(updateFields, filter);
    }

    private Map<String, Object> getParamMap() {
        if (paramMap != null) {
            return paramMap;
        }

        paramMap = filter == null ? new HashMap<String, Object>() : filter.getParamMap();
        paramMap.putAll(updateFields == null ? new HashMap<String, Object>() : updateFields);

        return paramMap;
    }

    public String getWhereSql(List<FieldDetail> columns) {
        Map<String, FieldDetail> columnMap = new HashMap<String, FieldDetail>();
        for (FieldDetail one : columns) {
            String fieldName = one.getFieldName();
            // 更新时，自定义where部分移除id字段，因为id已在SqlBuilder中写死
            if ("id".equalsIgnoreCase(fieldName)) {
                continue;
            }

            columnMap.put(one.getFieldName(), one);
        }
        return filter == null ? null : filter.toWhereSql(columnMap);
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

    public Map<String, Object> getUpdateFields() {
        return updateFields;
    }
}
