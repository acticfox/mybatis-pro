/*
 * $Id: Dialect.java 252 2013-05-30 03:23:10Z fanyong.kfy $
 *
 * Copyright (c) 2013 zhichubao.com. All Rights Reserved.
 */
package com.github.acticfox.base.mybatis.support.dialect;

/**
 * 数据库方言接口。
 *
 * @author fanyong.kong
 */
public interface Dialect {

    /**
     * 拼接SQL文‘limit’及'offset'数据
     * 
     * @param sql 原始SQL
     * @param offset 偏移量
     * @param limit 获取记录数
     * @return 拼接完成的SQL
     */
    public String getLimitString(String sql, int offset, int limit);
}
