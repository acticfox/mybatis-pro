/*
 * $Id: MySQLDialect.java 252 2013-05-30 03:23:10Z fanyong.kfy $
 *
 * Copyright (c) 2013 github.com. All Rights Reserved.
 */
package com.github.acticfox.base.mybatis.support.dialect;

/**
 * Dialect接口的MySQL实现，用于提供物理分页。
 *
 * @author fanyong.kong
 */
public class DefaultDialect implements Dialect {

    /**
     * SQL结束标记.
     */
    protected static final String SQL_END_DELIMITER = ";";

    /*
     * (non-Javadoc)
     * @see com.github.common.admin.dao.dialect.Dialect#getLimitString(java.lang.String, int, int)
     */
    public String getLimitString(String sql, int offset, int limit) {
        StringBuffer buffer = new StringBuffer(sql.length() + 20);
        buffer.append(trim(sql));
        buffer.append(" limit ");
        buffer.append(limit);
        if (offset > 0) {
            buffer.append(" offset ").append(offset);
        }
        buffer.append(SQL_END_DELIMITER);
        return buffer.toString();
    }

    /**
     * 切除末尾结束符
     *
     * @param sql 原SQL文
     * @return 切除结束符后的SQL
     */
    private String trim(String sql) {
        if (sql.endsWith(SQL_END_DELIMITER)) {
            return sql.substring(0,
                    sql.length() - 1 - SQL_END_DELIMITER.length());
        }
        return sql;
    } 
}
