package com.github.acticfox.base.mybatis.orm;

import java.lang.reflect.Field;

/**
 * 类 MappRule.java 的实现描述：TODO 类实现描述
 *
 * @author fanyong.kfy 14-5-19 上午9:53
 */
public interface Mapping {
    FieldDetail mapping(Field field);
    String transformName(String fieldName);
}
