/*
 * $Id$
 *
 * Copyright (c) 2013 github.com. All Rights Reserved.
 */
package com.github.acticfox.base.mybatis.support;

import java.sql.Statement;
import java.util.Properties;

import org.apache.ibatis.executor.resultset.ResultSetHandler;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.session.RowBounds;

/**
 * 物理分页支持类
 * <p/>
 * 在mybatis主配置文件进行如下配置
 * 
 * <pre>
 * &lt;plugins&gt;
 *      &lt;plugin interceptor="com.github.acticfox.base.orm.mybatis.support.ResultSetHandlerHandleResultSetsPlugin"&gt;&lt;/plugin&gt;
 * &lt;/plugins&gt;
 * </pre>
 * 
 * @see StatementHandlerInterceptor
 * 
 */
@Intercepts({@Signature(type = ResultSetHandler.class, method = "handleResultSets", args = {Statement.class})})
public class ResultSetHandlerInterceptor implements Interceptor {

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.ibatis.plugin.Interceptor#intercept(org.apache.ibatis.plugin.Invocation)
     */
    @Override
    public Object intercept(Invocation invocation) throws Throwable {

        // 获取处理目标
        ResultSetHandler target = (ResultSetHandler)invocation.getTarget();
        RowBounds rowBounds = (RowBounds)ReflectUtil.getFieldValue(target, "rowBounds");

        // 调整数据内容
        if (rowBounds.getLimit() > 0 && rowBounds.getLimit() < RowBounds.NO_ROW_LIMIT) {
            ReflectUtil.setFieldValue(target, "rowBounds", new RowBounds());
        }
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
        // TODO Auto-generated method stub
    }
}
