/*
 * Copyright 2013 zhichubao.com All right reserved. This software is the confidential and proprietary information of
 * zhichubao.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it
 * only in accordance with the terms of the license agreement you entered into with zhichubao.com .
 */
package com.github.acticfox.base.mybatis.orm.handler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;

import com.github.acticfox.common.api.enums.StringEnum;

/**
 * 类TypeHandler.java的实现描述： 通用枚举类型转换器
 * 
 * @author fanyong 2013-8-30 下午2:08:05
 */
@MappedJdbcTypes(JdbcType.VARCHAR)
public class StringTypeHandler extends BaseTypeHandler<StringEnum> {

    private Class<StringEnum> type;

    public StringTypeHandler(Class<StringEnum> type) {
        if (type == null)
            throw new IllegalArgumentException("Type argument cannot be null");
        this.type = type;
    }

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, StringEnum eifEnum, JdbcType jdbcType)
        throws SQLException {
        ps.setString(i, eifEnum.getCode());
    }

    @Override
    public StringEnum getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return convert(rs.getString(columnName));
    }

    @Override
    public StringEnum getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return convert(rs.getString(columnIndex));
    }

    @Override
    public StringEnum getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return convert(cs.getString(columnIndex));
    }

    private StringEnum convert(String code) {
        StringEnum[] enums = type.getEnumConstants();
        if (enums == null) {
            return null;
        }
        for (StringEnum em : enums) {
            if (em.getCode().equals(code)) {
                return em;
            }
        }

        return null;
    }
}
