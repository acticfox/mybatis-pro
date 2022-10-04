package com.github.acticfox.base.mybatis;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ibatis.session.SqlSession;
import org.mybatis.spring.support.SqlSessionDaoSupport;

import com.github.acticfox.base.dao.DataRowHandler;
import com.github.acticfox.base.dao.QueryRowHandlerDAO;
import com.github.acticfox.base.mybatis.session.ResultHandlerWrapper;

/**
 * QueryRowHandlerDAO接口MyBatis实现类。
 * <p/>
 * 该类在Bean定义文件中定义，并注入到Service层使用，使用方式如下: <fieldset style="border:1pt solid black;padding:10px;width:100%;">
 * <legend>Bean定义文件</legend>
 * 
 * <pre>
 * &lt;bean id="sendmailBLogic"
 *     class="com.github.acticfox.base.sample.blogic.SendmailBLogic"&gt;
 *     &lt;property name="queryRowHandlerDAO" ref="queryRowHandlerDAO" /&gt;
 * &lt;/bean&gt;
 * 
 * &lt;bean id="<b>queryRowHandlerDAO</b>"
 *     class="<b>com.github.acticfox.base.orm.mybatis.QueryRowHandlerDAOMyBatisImpl</b>"&gt;
 *     &lt;property name="sqlSessionFactory" ref="sqlSessionFactory" /&gt;
 * &lt;/bean&gt;
 * 
 * &lt;bean id="sqlSessionFactory" class="org.mybatis.spring.SqlSessionFactoryBean"&gt;
 *     &lt;property name="dataSource" ref="dataSource" /&gt;
 *     &lt;property name="configLocation" value="classpath:mybatis-config.xml" /&gt;
 * &lt;/bean&gt;
 * </pre>
 * 
 * </fieldset> <fieldset style="border:1pt solid black;padding:10px;width:100%;"> <legend>Service使用方法</legend>
 * 
 * <pre>
 * public class SendmailBLogic {
 *     protected QueryRowHandlerDAO queryRowHandlerDAO = null;
 * 
 *     public void setQueryRowHandlerDAO(QueryRowHandlerDAO queryRowHandlerDAO) {
 *         this.queryRowHandlerDAO = queryRowHandlerDAO;
 *     }
 * 
 *     public String execute(ActionForm form) {
 * 
 *         Map&lt;Sring, Object&gt; bindParams = new HashMap&lt;String, Object&gt;();
 *         bindParams.put("level", form.getUserLevel());
 *         <b>queryRowHandlerDAO.executeWithRowHandler("user.getUser", bindParams, new DataRowHandler&lt;User&gt;() {
 *             public void handleRow(User user) {
 *                 Sender.send(user.getEmail(), message);
 *             }
 *         });</b>
 *         ...
 *         return "success";
 *     }
 * }
 * </pre>
 * 
 * </fieldset>
 * 
 * @see DataRowHandler
 */
public class QueryRowHandlerDAOMyBatisImpl extends SqlSessionDaoSupport implements QueryRowHandlerDAO {

    /** 日志 */
    static Log log = LogFactory.getLog(QueryRowHandlerDAOMyBatisImpl.class);

    @Override
    @SuppressWarnings({"rawtypes", "unchecked"})
    public void executeWithRowHandler(String sqlID, Object bindParams, DataRowHandler dataResultHandler) {

        if (log.isDebugEnabled()) {
            log.debug("executeWithRowHandler Start.");
        }

        SqlSession sqlSession = getSqlSession();
        sqlSession.select(sqlID, bindParams, new ResultHandlerWrapper(dataResultHandler));

        if (log.isDebugEnabled()) {
            log.debug("executeWithRowHandler End.");
        }
    }
}
