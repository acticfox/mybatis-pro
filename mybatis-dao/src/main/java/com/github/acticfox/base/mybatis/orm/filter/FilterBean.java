/*
 * Copyright 2015 github.com All right reserved. This software is the
 * confidential and proprietary information of github.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with github.com .
 */
package com.github.acticfox.base.mybatis.orm.filter;

import java.io.Serializable;

import com.github.acticfox.base.mybatis.orm.filter.enums.CompareOp;
import com.github.acticfox.base.mybatis.orm.filter.enums.Relation;

/**
 * 类FilterBean.java的实现描述： 该类只是一个简单的POJO，作为存储过滤器的容器其目的是对于不同的ORM做为一个中间数据层以使本框架
 * 在不同的ORM中转换数据。
 * <p>
 * 注意：<code>Sorter</code>没有提供类似的容器,因为排序器本身已提供这样容器的功能
 * 
 * @author fanyong.kfy 2015年5月4日 下午2:00:01
 */
public class FilterBean implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 数据库表的字段名
     */
    private String fieldName;

    /**
     * 值
     */
    private Object value;

    /**
     * 操作符
     */
    private CompareOp operater;

    /**
     * 关系符
     */
    private Relation relations;

    /**
     * 前置NOT操作符
     */
    private boolean not;

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public CompareOp getOperater() {
        return operater;
    }

    public void setOperater(CompareOp operater) {
        this.operater = operater;
    }

    public Relation getRelations() {
        return relations;
    }

    public void setRelations(Relation relations) {
        this.relations = relations;
    }

    public boolean isNot() {
        return not;
    }

    public void setNot(boolean not) {
        this.not = not;
    }

}
