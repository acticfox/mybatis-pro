/*
 * Copyright 2014 github.com All right reserved. This software is the
 *    confidential and proprietary information of github.com ("Confidential
 *    Information"). You shall not disclose such Confidential Information and shall
 *    use it only in accordance with the terms of the license agreement you entered
 *    into with github.com .
 */

package com.github.acticfox.base.mybatis.orm;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Table;

import org.apache.commons.lang.StringUtils;
import org.springframework.core.annotation.AnnotationUtils;

/**
 * 类 TableHandler.java 的实现描述：TODO 类实现描述
 * 
 * @author fanyong.kfy 14-1-16 下午5:05
 */
public class TableHandler {
    private List<FieldDetail> validColumns = new ArrayList<FieldDetail>(); //需要映射的field
    private Set<String>       fieldNameSet = new HashSet<String>();       //去除重复的field 判断
    private String            tableName;
    private Mapping           mapping      = new DefaultMapping();

    public TableHandler(Class<?> clazz) {
        // 读取Table注解
        Table table = AnnotationUtils.getAnnotation(clazz, Table.class);
        String tableName = table == null ? null : table.name();
        if (StringUtils.isBlank(tableName)) {
            // 如果Table注解中没有指定Name，那就采用默认规则转换
            tableName = mapping.transformName(clazz.getSimpleName());
        }
        this.tableName = tableName;
        initColumn(clazz);
    }

    private void initColumn(Class<?> clazz) {
        if (clazz == null) {
            return;
        }
        for (Field field : clazz.getDeclaredFields()) {
            FieldDetail fieldDetail = mapping.mapping(field);
            if (fieldDetail.isColumn() && !fieldNameSet.contains(fieldDetail.getFieldName())) {
                validColumns.add(fieldDetail);
                fieldNameSet.add(fieldDetail.getFieldName());
            }
        }
        initColumn(clazz.getSuperclass());
    }

    public List<FieldDetail> getColumns() {
        return validColumns;
    }

    public String getTableName() {
        return tableName;
    }
}
