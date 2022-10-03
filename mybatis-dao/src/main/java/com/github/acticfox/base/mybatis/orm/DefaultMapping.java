package com.github.acticfox.base.mybatis.orm;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Transient;

import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.type.TypeHandler;

import com.github.acticfox.base.mybatis.orm.handler.IntTypeHandler;
import com.github.acticfox.base.mybatis.orm.handler.JsonTypeHandler;
import com.github.acticfox.base.mybatis.orm.handler.StringTypeHandler;
import com.github.acticfox.common.api.annotation.FieldType;

/**
 * 类 DefaultMapping.java 的实现描述：TODO 类实现描述
 * 
 * @author fanyong.kfy 14-5-19 上午10:13
 */
public class DefaultMapping implements Mapping {
    @Override
    public FieldDetail mapping(Field field) {
        FieldDetail.Builder builder = new FieldDetail.Builder();
        builder.field(field);

        Transient tr = field.getAnnotation(Transient.class);
        builder.isColumn(
            tr == null && !(Modifier.isStatic(field.getModifiers()) || Modifier.isFinal(field.getModifiers())));

        Column column = field.getAnnotation(Column.class);
        builder.updateAble(column == null || column.updatable());
        builder.insertAble(column == null || column.insertable());
        Id pk = field.getAnnotation(Id.class);
        if (pk != null) {
            builder.isPk(true);
        }
        Class<? extends TypeHandler> typeHandler = null;
        FieldType fieldType = field.getAnnotation(FieldType.class);
        if (fieldType != null) {
            switch (fieldType.value()) {
                case IntEnum:
                    typeHandler = IntTypeHandler.class;
                    break;
                case StringEnum:
                    typeHandler = StringTypeHandler.class;
                    break;
                case JSON:
                    typeHandler = JsonTypeHandler.class;
                    break;
            }
        }
        builder.typeHandler(typeHandler);

        if (column != null && StringUtils.isNotBlank(column.name())) {
            builder.columnName(column.name());
        } else {
            builder.columnName(transformName(field.getName()));
        }

        return builder.build();
    }

    /**
     * 属性名 转 列名 类名 转 表名
     * <p/>
     * 
     * <pre>
     *   transformName("UserExtend") = user_extend
     *   transformName("userId") = user_id
     *   transformName("userID") = user_i_d
     *   transformName("UserId") = user_id
     * </pre>
     * 
     * @param name
     * @return
     */
    public String transformName(String name) {
        StringBuilder builder = new StringBuilder();
        builder.append(Character.toLowerCase(name.charAt(0)));
        for (int i = 1; i < name.length(); i++) {
            char charAt = name.charAt(i);
            if (Character.isUpperCase(charAt)) {
                builder.append("_");
                builder.append(Character.toLowerCase(charAt));
            } else {
                builder.append(charAt);
            }
        }
        return builder.toString();
    }
}
