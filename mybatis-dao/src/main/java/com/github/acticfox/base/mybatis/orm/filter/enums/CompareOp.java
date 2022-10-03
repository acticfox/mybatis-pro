/*
 * Copyright 2014 zhichubao.com All right reserved. This software is the
 * confidential and proprietary information of zhichubao.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with zhichubao.com .
 */
package com.github.acticfox.base.mybatis.orm.filter.enums;

/**
 * 类CompareType.java的实现描述：比较类型
 * 
 * @author fanyong.kfy 2014-05-24 11:55
 */
public enum CompareOp {
    LT("<"),
    LTE("<="),
    EQ("="),
    NEQ("<>"),
    GT(">"),
    GTE(">="),
    IN("in"),
    NOT_IN("not in"),
    Like("like"),
    LeftLike("like"),
    RightLike("like"),
    NotLike("not like"),
    NotLeftLike("not like"),
    NotRightLike("not like"),
    IsNull("is null"),
    IsNotNull("is not null");

    private String opStr;

    private CompareOp(String opStr) {
        this.opStr = opStr;
    }

    public String getOpStr() {
        return opStr;
    }
}
