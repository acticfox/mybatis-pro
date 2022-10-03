/*
 * Copyright 2014 zhichubao.com All right reserved. This software is the
 * confidential and proprietary information of zhichubao.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with zhichubao.com .
 */
package com.github.acticfox.base.mybatis.orm.filter.enums;

import org.apache.commons.lang.StringUtils;

/**
 * 类OrderType.java的实现描述：排序类型
 * 
 * @author fanyong.kfy 2014-05-23 17:28
 */
public enum SortOrder {
    ASC("asc"),
    DESC("desc"),
    Rand("rand()");

    private SortOrder(String op) {
        this.op = op;
    }

    private String op;

    public String getOp() {
        return op;
    }

    public static SortOrder valuesOf(String order) {
        if (StringUtils.isEmpty(order)) {
            return null;
        }
        for (SortOrder sortOrder : SortOrder.values()) {
            if (order.equalsIgnoreCase(sortOrder.op)) {
                return sortOrder;
            }
        }
        return null;
    }

    public static void main(String[] args) {
        SortOrder sortOrderEnum = SortOrder.valuesOf("asc");
        System.out.println(sortOrderEnum);

    }
}
