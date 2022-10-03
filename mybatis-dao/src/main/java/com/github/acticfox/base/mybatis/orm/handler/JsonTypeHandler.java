package com.github.acticfox.base.mybatis.orm.handler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.google.common.base.Preconditions;

/**
 * 类 JsonTypeHandler.java 的实现描述：JsonTypeHandler
 *
 * @author fanyong.kfy 14-5-19 下午2:31
 */
public class JsonTypeHandler extends BaseTypeHandler<Object> {

    private Class<Object> type;

    public JsonTypeHandler(Class<Object> type) {
        Preconditions.checkArgument(type != null, "Type argument can't be null");
        this.type = type;
    }

    @Override
    public void setNonNullParameter(PreparedStatement preparedStatement, int i, Object obj, JdbcType jdbcType)
            throws SQLException {
        preparedStatement.setString(i, JSON.toJSONString(obj, SerializerFeature.DisableCircularReferenceDetect));
    }

    @Override
    public Object getNullableResult(ResultSet resultSet, String s) throws SQLException {
        return convert(resultSet.getString(s));
    }

    @Override
    public Object getNullableResult(ResultSet resultSet, int i) throws SQLException {
        return convert(resultSet.getString(i));
    }

    @Override
    public Object getNullableResult(CallableStatement callableStatement, int i) throws SQLException {
        return convert(callableStatement.getString(i));
    }

    private Object convert(String value) throws SQLException {
        if (StringUtils.isBlank(value)) {
            return null;
        }
        try {
            return JSON.parseObject(value, type);
        } catch (Exception e) {
            throw new SQLException("value cannot convert to json", e);
        }
    }
}
