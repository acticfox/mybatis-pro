/*
 * $Id$
 *
 * Copyright (c) 2013 zhichubao.com. All Rights Reserved.
 */
package com.github.acticfox.base.mybatis.support;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.SQLWarning;
import java.util.Properties;
import java.util.UUID;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ibatis.executor.statement.BaseStatementHandler;
import org.apache.ibatis.executor.statement.RoutingStatementHandler;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.session.RowBounds;
import org.springframework.jdbc.SQLWarningException;

import com.github.acticfox.base.mybatis.orm.SqlBuilder;
import com.github.acticfox.base.mybatis.support.dialect.DefaultDialect;
import com.github.acticfox.base.mybatis.support.dialect.Dialect;

/**
 * 物理分页支持类
 * <p/>
 * 在mybatis主配置文件进行如下配置
 * 
 * <pre>
 * &lt;plugins&gt;
 *      &lt;plugin interceptor="com.zhichubao.base.orm.mybatis.support.StatementHandlerPreparePlugin"&gt;&lt;/plugin&gt;
 * &lt;/plugins&gt;
 * </pre>
 * 
 * 这里默认使用{@link DefaultDialect}进行SQL处理，使用者可自行实现{@link Dialect} 接口以应对不同的数据库的SQL规则，并在配置项中指定实现类。
 * 
 * <pre>
 * &lt;plugins&gt;
 *      &lt;plugin interceptor="com.zhichubao.base.orm.mybatis.support.StatementHandlerPreparePlugin"&gt;
 *          &lt;property name="dialectClass" value="com.zhichubao.mynet.base.orm.mybatis.support.dialect.OracleDialect"/&gt;
 *      &lt;/plugin&gt;
 * &lt;/plugins&gt;
 * </pre>
 * 
 * @see ResultSetHandlerInterceptor
 */
@Intercepts({@Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class})})
public class StatementHandlerInterceptor implements Interceptor {

    /** 日志 */
    static Log log = LogFactory.getLog(StatementHandlerInterceptor.class);

    /** 分页处理程序 */
    private Dialect dialect;

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.ibatis.plugin.Interceptor#intercept(org.apache.ibatis.plugin .Invocation)
     */
    @Override
    public Object intercept(Invocation invocation) throws Throwable {

        // 获取处理目标
        StatementHandler target = (StatementHandler)invocation.getTarget();
        if (target instanceof RoutingStatementHandler) {
            target = (BaseStatementHandler)ReflectUtil.getFieldValue(target, "delegate");
        }
        RowBounds rowBounds = (RowBounds)ReflectUtil.getFieldValue(target, "rowBounds");

        // 调整查询字符串
        BoundSql boundSql = target.getBoundSql();
        String sql = boundSql.getSql();
        if (rowBounds.getLimit() > 0 && rowBounds.getLimit() < RowBounds.NO_ROW_LIMIT) {
            sql = dialect.getLimitString(sql, rowBounds.getOffset(), rowBounds.getLimit());
            ReflectUtil.setFieldValue(boundSql, "sql", sql);
        }
        if (StringUtils.indexOfIgnoreCase(sql, "SELECT") > -1) {
            if (StringUtils.indexOfIgnoreCase(sql, "WHERE") < 0 && StringUtils.indexOfIgnoreCase(sql, "LIMIT") < 0) {
                log.warn("sql:" + sql + "无查询条件");
                throw new SQLWarningException("查询条件为空", new SQLWarning("查询条件为空"));
            }
        }
        if (StringUtils.indexOfIgnoreCase(sql, "INSERT") > -1) {
            Object obj = boundSql.getParameterObject();
            SqlBuilder sqlBuilder = new SqlBuilder(obj.getClass());
            Field field = ReflectUtil.getDeclaredField(obj, sqlBuilder.getPrimaryKeyFieldName());
            if (field != null) {
                GeneratedValue generatedValue = field.getAnnotation(GeneratedValue.class);
                if (generatedValue != null && generatedValue.strategy() == GenerationType.AUTO
                    && "system-uuid".equals(generatedValue.generator())) {
                    String uuid = UUID.randomUUID().toString().replaceAll("-", "");
                    ReflectUtil.setFieldValue(obj, sqlBuilder.getPrimaryKeyFieldName(), uuid);
                }
            }
        }

        // 执行查询处理
        return invocation.proceed();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.ibatis.plugin.Interceptor#plugin(java.lang.Object)
     */
    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.ibatis.plugin.Interceptor#setProperties(java.util.Properties)
     */
    @Override
    public void setProperties(Properties properties) {

        String dialectClass = properties.getProperty("dialectClass");

        // 初始化物理查询处理程序
        if (dialectClass == null || dialectClass.isEmpty()) {
            dialect = new DefaultDialect();
        } else {
            try {
                dialect = (Dialect)Class.forName(dialectClass).newInstance();
            } catch (Exception e) {
                throw new IllegalArgumentException("Invalid dialect class " + dialectClass, e);
            }
        }
    }
}
