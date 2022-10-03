package com.github.acticfox.base.mybatis.session;

import org.apache.ibatis.session.ResultContext;
import org.apache.ibatis.session.ResultHandler;

import com.github.acticfox.base.dao.DataRowHandler;

public class ResultHandlerWrapper implements ResultHandler {

    /**
     * 查询结果处理类
     */
    protected DataRowHandler<Object> dataRowHandler = null;

    /**
     * 构造函数
     * 
     * @param dataRowHandler 查询结果处理类
     */
    public ResultHandlerWrapper(DataRowHandler<Object> dataRowHandler) {
        this.dataRowHandler = dataRowHandler;
    }

    @Override
    public void handleResult(ResultContext context) {
        this.dataRowHandler.handleRow(context.getResultObject());
    }
}
