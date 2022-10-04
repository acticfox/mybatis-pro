/*
 * $Id$
 *
 * Copyright (c) 2013 github.com. All Rights Reserved.
 */
package com.github.acticfox.base.mybatis;

import java.util.List;

import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.support.SqlSessionDaoSupport;
import org.springframework.jdbc.JdbcUpdateAffectedIncorrectNumberOfRowsException;

import com.github.acticfox.base.dao.SqlHolder;
import com.github.acticfox.base.dao.UpdateDAO;
import com.google.common.base.Throwables;

/**
 * UpdateDAO接口MyBatis实现类。
 * <p/>
 * 该类在Bean定义文件中定义，并注入到Service层使用，使用方式如下: <fieldset style="border:1pt solid black;padding:10px;width:100%;">
 * <legend>Bean定义文件</legend>
 * 
 * <pre>
 * &lt;bean id="registBLogic"
 *     class="com.github.acticfox.base.sample.blogic.RegistBLogic"&gt;
 *     &lt;property name="updateDAO" ref="updateDAO" /&gt;
 * &lt;/bean&gt;
 * 
 * &lt;bean id="<b>updateDAO</b>"
 *     class="<b>com.github.acticfox.base.orm.mybatis.UpdateDAOMyBatisImpl</b>"&gt;
 *     &lt;property name="sqlSessionFactory" ref="sqlSessionFactory" /&gt;
 * &lt;/bean&gt;
 * 
 * &lt;bean id="sqlSessionFactory" class="org.mybatis.spring.SqlSessionFactoryBean"&gt;
 *     &lt;property name="dataSource" ref="dataSource" /&gt;
 *     &lt;property name="configLocation" value="classpath:mybatis-config.xml" /&gt;
 * &lt;/bean&gt;
 * </pre>
 * 
 * </fieldset> <fieldset style="border:1pt solid black;padding:10px;width:100%;"> <legend>Service使用方法: 更新单条记录</legend>
 * 
 * <pre>
 * public class RegistBLogic {
 *     private UpdateDAO updateDAO = null;
 * 
 *     public void setUpdateDAO(UpdateDAO updateDAO) {
 *         this.updateDAO = updateDAO;
 *     }
 * 
 *     public String execute() {
 *         UserBean bean = new UserBean();
 *         bean.setId(&quot;1&quot;);
 *         bean.setName(&quot;N.OUNO&quot;);
 *         bean.setAge(&quot;20&quot;);
 * 
 *         &lt;b&gt;updateDAO.execute(&quot;user.insertUser&quot;, bean);&lt;/b&gt;
 *         ...
 *         return &quot;success&quot;;
 *     }
 * }
 * </pre>
 * 
 * </fieldset> <fieldset style="border:1pt solid black;padding:10px;width:100%;"> <legend>Service层使用方法：联机批量处理</legend>
 * 
 * <pre>
 * public String execute() {
 *     List&lt;SqlHolder&gt; sqlHolders = new ArrayList&lt;SqlHolder&gt;();
 * 
 *     UserBean bean = new UserBean();
 *     bean.setId("1");
 *     bean.setName("N.OUNO");
 *     bean.setAge("20");
 *     SqlHolder holder = new SqlHolder("insertUser", bean);
 *     sqlHolders.add(holder);
 *
 *     UserBean bean2 = new UserBean();
 *     bean.setId("2");
 *     bean.setName("K.FUJIMOTO");
 *     bean.setAge("21");
 *     SqlHolder holder2 = new SqlHolder("insertUser", bean2);
 *     sqlHolders.add(holder2);
 * 
 *     <b>dao.executeBatch(sqlHolders);</b>
 *     ...
 *     return "success";
 * }
 * </pre>
 * 
 * </fieldset>
 */
public class UpdateDAOMyBatisImpl extends SqlSessionDaoSupport implements UpdateDAO {

    @Override
    public int execute(String sqlID, Object bindParams) {

        if (logger.isDebugEnabled()) {
            logger.debug("execute Start.");
        }

        // 执行SQL: 更新数据
        int row = getSqlSession().update(sqlID, bindParams);

        if (logger.isDebugEnabled()) {
            logger.debug("execute End. success count:" + row);
        }

        return row;
    }

    @Override
    public int executeUnique(String sqlID, Object bindParams) {
        if (logger.isDebugEnabled()) {
            logger.debug("execute Start.");
        }

        // 执行SQL: 更新数据
        int row = getSqlSession().update(sqlID, bindParams);
        if (row > 1) {
            throw new JdbcUpdateAffectedIncorrectNumberOfRowsException(sqlID, 1, row);
        }

        if (logger.isDebugEnabled()) {
            logger.debug("execute End. success count:" + row);
        }
        return row;
    }

    @Override
    public int executeBatch(List<SqlHolder> sqlHolders) {

        // 执行SQL Batch
        Integer result = 0;

        StringBuilder logStr = new StringBuilder();
        if (logger.isDebugEnabled()) {
            logger.debug("Batch SQL count:" + sqlHolders.size());
        }

        SqlSessionFactory sessionFactory = ((SqlSessionTemplate)super.getSqlSession()).getSqlSessionFactory();
        SqlSession sqlSession = sessionFactory.openSession(ExecutorType.BATCH, true);

        // 新建Session
        // 创建或重用Connection
        try {
            for (SqlHolder sqlHolder : sqlHolders) {
                int ret = sqlSession.update(sqlHolder.getSqlID(), sqlHolder.getBindParams());
                result = result + ret;

                if (logger.isDebugEnabled()) {
                    logStr.setLength(0);
                    logStr.append("Call update sql. - SQL_ID:'");
                    logStr.append(sqlHolder.getSqlID());
                    logStr.append("' Parameters:");
                    logStr.append(sqlHolder.getBindParams());
                    logger.debug(logStr.toString());
                }
            }
            sqlSession.commit();
        } catch (RuntimeException e) {
            // 批处理异常
            logger.error("!!!ExecuteBatch FAILED!!!", e);

            try {
                sqlSession.rollback();
            } catch (Exception ex) {
                // 回滚失败
                logger.error("!!!ExecuteBatch ROLLBACK FAILED!!!", e);
            }
            // 抛出异常
            Throwables.propagate(e);
        } finally {
            try {
                sqlSession.close();
            } catch (Exception e) {
                logger.error("!!!CLOSE FAILED!!!", e);
            }
        }

        if (logger.isDebugEnabled()) {
            logger.debug("executeBatch complete. Result:" + result);
        }

        return result.intValue();
    }

}
