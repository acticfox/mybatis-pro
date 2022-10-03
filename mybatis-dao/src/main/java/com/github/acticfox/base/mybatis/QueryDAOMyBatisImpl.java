/*
 * $Id$
 *
 * Copyright (c) 2013 zhichubao.com. All Rights Reserved.
 */
package com.github.acticfox.base.mybatis;

import java.lang.reflect.Array;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ibatis.session.RowBounds;
import org.mybatis.spring.support.SqlSessionDaoSupport;

import com.github.acticfox.base.dao.IllegalClassTypeException;
import com.github.acticfox.base.dao.QueryDAO;

/**
 * QueryDAO接口MyBatis实现类。
 * <p/>
 * 该类在Bean定义文件中定义，并注入到Service层使用，使用方式如下: <fieldset style="border:1pt solid black;padding:10px;width:100%;">
 * <legend>Bean定义文件</legend>
 * 
 * <pre>
 * &lt;bean id="listBLogic"
 *     class="com.zhichubao.base.sample.blogic.ListBLogic"&gt;
 *     &lt;property name="queryDAO" ref="queryDAO" /&gt;
 * &lt;/bean&gt;
 * 
 * &lt;bean id="<b>queryDAO</b>"
 *     class="<b>com.zhichubao.base.orm.mybatis.QueryDAOMyBatisImpl</b>"&gt;
 *     &lt;property name="sqlSessionFactory" ref="sqlSessionFactory" /&gt;
 * &lt;/bean&gt;
 * 
 * &lt;bean id="sqlSessionFactory" class="org.mybatis.spring.SqlSessionFactoryBean"&gt;
 *     &lt;property name="dataSource" ref="dataSource" /&gt;
 *     &lt;property name="configLocation" value="classpath:mybatis-config.xml" /&gt;
 * &lt;/bean&gt;
 * </pre>
 * 
 * </fieldset> <fieldset style="border:1pt solid black;padding:10px;width:100%;"> <legend>Service使用方法: 获取单条记录</legend>
 * 
 * <pre>
 * public class ListBLogic {
 *     protected QueryDAO queryDAO = null;
 * 
 *     public void setQueryDAO(QueryDAO queryDAO) {
 *         this.queryDAO = queryDAO;
 *     }
 * 
 *     public String execute(ActionForm form) {
 *         UserBean bean = <b>queryDAO.executeForObject("user.getUser","10000000",UserBean.class);</b>
 *         ...
 *         return "success";
 *     }
 * }
 * </pre>
 * 
 * </fieldset> <fieldset style="border:1pt solid black;padding:10px;width:100%;"> <legend>Service使用方法:
 * 获取全部多条记录列表</legend>
 * 
 * <pre>
 * public String execute(ActionForm form) {</br>
 *     List<UserBean> bean = <b>queryDAO.executeForObjectList("user.getUser","10000000");</b>
 *     ...
 *     return "success";
 * }
 * </pre>
 * 
 * </fieldset> <fieldset style="border:1pt solid black;padding:10px;width:100%;"> <legend>Service使用方法:
 * 获取全部多条记录数组</legend>
 * 
 * <pre>
 * public String execute(ActionForm form) {
 *     UserBean[] bean = <b>queryDAO.executeForObjectArray("user.getUser","10000000", UserBean.class);</b>
 *     ...
 *     return "success";
 * }
 * </pre>
 * 
 * </fieldset>
 * <p/>
 * <fieldset style="border:1pt solid black;padding:10px;width:100%;"> <legend>Service使用方法: 获取指定数量多条记录列表</legend>
 * 参数为记录读取开始索引和取回数量，如从第21条开始取回10条记录
 * 
 * <pre>
 * public String execute(ActionForm form) {
 *     List<UserBean> bean = <b>queryDAO.executeForObjectList("user.getUser","10000000", 20, 10);</b>
 *     ...
 *     return "success";
 * }
 * </pre>
 * 
 * </fieldset> <fieldset style="border:1pt solid black;padding:10px;width:100%;"> <legend> Service使用方法:
 * 获取指定数量多条记录数组</legend> 参数为记录读取开始索引和取回数量，如从第21条开始取回10条记录
 * 
 * <pre>
 * public String execute(ActionForm form) {</br>
 *     UserBean[] bean = <b>queryDAO.executeForObjectArray("user.getUser","10000000",UserBean.class, 20, 10);</b>
 *     ...
 *     return "success";
 * }
 * </pre>
 * 
 * </fieldset>
 */
public class QueryDAOMyBatisImpl extends SqlSessionDaoSupport implements QueryDAO {

    /** 日志 */
    static Log log = LogFactory.getLog(QueryDAOMyBatisImpl.class);

    @Override
    public <E> E executeForObject(String sqlID, Object bindParams, Class<E> clazz) {

        if (log.isDebugEnabled()) {
            log.debug("executeForObject Start.");
        }

        E rObj = null;
        try {
            // 执行SQL：取得记录
            Object obj = getSqlSession().selectOne(sqlID, bindParams);
            if (log.isDebugEnabled() && obj != null) {
                log.debug("Return type:" + obj.getClass().getName());
            }
            if (clazz != null && obj != null) {
                rObj = (E)clazz.cast(obj);
            }
        } catch (ClassCastException e) {
            log.error(IllegalClassTypeException.ERROR_ILLEGAL_CLASS_TYPE);
            throw new IllegalClassTypeException(e);
        }

        if (log.isDebugEnabled()) {
            log.debug("executeForObject End.");
        }

        return rObj;
    }

    public Map<String, Object> executeForMap(String sqlID, Object bindParams) {

        if (log.isDebugEnabled()) {
            log.debug("executeForMap Start.");
        }

        @SuppressWarnings("unchecked")
        Map<String, Object> rObj = this.executeForObject(sqlID, bindParams, Map.class);

        if (log.isDebugEnabled()) {
            log.debug("executeForMap End.");
        }

        return rObj;
    }

    @SuppressWarnings("unchecked")
    public <E> E[] executeForObjectArray(String sqlID, Object bindParams, Class<E> clazz) {

        if (log.isDebugEnabled()) {
            log.debug("executeForObjectArray Start.");
        }

        if (clazz == null) {
            log.error(IllegalClassTypeException.ERROR_ILLEGAL_CLASS_TYPE);
            throw new IllegalClassTypeException();
        }

        E[] retArray;
        try {
            // 执行SQL：取得记录
            List<E> list = getSqlSession().selectList(sqlID, bindParams);
            retArray = (E[])Array.newInstance(clazz, list.size());
            list.toArray(retArray);
        } catch (ArrayStoreException e) {
            log.error(IllegalClassTypeException.ERROR_ILLEGAL_CLASS_TYPE);
            throw new IllegalClassTypeException(e);
        }

        if (log.isDebugEnabled()) {
            log.debug("executeForObjectArray End.");
        }

        return retArray;
    }

    public Map<String, Object>[] executeForMapArray(String sqlID, Object bindParams) {

        if (log.isDebugEnabled()) {
            log.debug("executeForMapArray Start.");
        }

        @SuppressWarnings("unchecked")
        Map<String, Object>[] map = executeForObjectArray(sqlID, bindParams, Map.class);

        if (log.isDebugEnabled()) {
            log.debug("executeForMapArray End.");
        }

        return map;
    }

    @SuppressWarnings("unchecked")
    public <E> E[] executeForObjectArray(String sqlID, Object bindParams, Class<E> clazz, int beginIndex,
        int maxCount) {

        if (log.isDebugEnabled()) {
            log.debug("executeForObjectArray Start.");
        }

        if (clazz == null) {
            log.error(IllegalClassTypeException.ERROR_ILLEGAL_CLASS_TYPE);
            throw new IllegalClassTypeException();
        }

        E[] retArray;
        try {
            // 执行SQL：取得记录
            List<E> list = getSqlSession().selectList(sqlID, bindParams, new RowBounds(beginIndex, maxCount));

            retArray = (E[])Array.newInstance(clazz, list.size());
            list.toArray(retArray);
        } catch (ArrayStoreException e) {
            log.error(IllegalClassTypeException.ERROR_ILLEGAL_CLASS_TYPE);
            throw new IllegalClassTypeException(e);
        }

        if (log.isDebugEnabled()) {
            log.debug("executeForObjectArray End.");
        }

        return retArray;
    }

    public Map<String, Object>[] executeForMapArray(String sqlID, Object bindParams, int beginIndex, int maxCount) {

        if (log.isDebugEnabled()) {
            log.debug("executeForMapArray Start.");
        }

        @SuppressWarnings("unchecked")
        Map<String, Object>[] map = executeForObjectArray(sqlID, bindParams, Map.class, beginIndex, maxCount);

        if (log.isDebugEnabled()) {
            log.debug("executeForMapArray End.");
        }

        return map;
    }

    @SuppressWarnings("unchecked")
    public <E> List<E> executeForObjectList(String sqlID, Object bindParams) {

        if (log.isDebugEnabled()) {
            log.debug("executeForObjectList Start.");
        }

        // 执行SQL：取得记录
        List<E> list = getSqlSession().selectList(sqlID, bindParams);

        if (log.isDebugEnabled()) {
            log.debug("executeForObjectList End.");
        }

        return list;
    }

    public List<Map<String, Object>> executeForMapList(String sqlID, Object bindParams) {

        if (log.isDebugEnabled()) {
            log.debug("executeForMapList Start.");
        }

        List<Map<String, Object>> mapList = executeForObjectList(sqlID, bindParams);

        if (log.isDebugEnabled()) {
            log.debug("executeForMapList End.");
        }

        return mapList;
    }

    @SuppressWarnings("unchecked")
    public <E> List<E> executeForObjectList(String sqlID, Object bindParams, int beginIndex, int maxCount) {

        if (log.isDebugEnabled()) {
            log.debug("executeForObjectList Start.");
        }

        // 执行SQL：取得记录
        List<E> list = getSqlSession().selectList(sqlID, bindParams, new RowBounds(beginIndex, maxCount));

        if (log.isDebugEnabled()) {
            log.debug("executeForObjectList End.");
        }

        return list;
    }

    public List<Map<String, Object>> executeForMapList(String sqlID, Object bindParams, int beginIndex, int maxCount) {

        if (log.isDebugEnabled()) {
            log.debug("executeForMapList Start.");
        }

        List<Map<String, Object>> mapList = executeForObjectList(sqlID, bindParams, beginIndex, maxCount);

        if (log.isDebugEnabled()) {
            log.debug("executeForMapList End.");
        }

        return mapList;
    }
}
