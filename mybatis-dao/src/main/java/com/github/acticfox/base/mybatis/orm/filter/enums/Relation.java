/*
 * Copyright 2015 github.com All right reserved. This software is the
 * confidential and proprietary information of github.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with github.com .
 */
package com.github.acticfox.base.mybatis.orm.filter.enums;

/**
 * 类Relation.java的实现描述：
 * 
 * <pre>
 * SQL 连接符
 * </pre>
 * 
 * @author fanyong.kfy 2015年5月7日 下午5:01:49
 */
public enum Relation {

    AND(" AND "),
    OR(" OR ");
    private String rname;

    private Relation(String rname) {
        this.rname = rname;
    }

    public String getRname() {
        return rname;
    }

}
