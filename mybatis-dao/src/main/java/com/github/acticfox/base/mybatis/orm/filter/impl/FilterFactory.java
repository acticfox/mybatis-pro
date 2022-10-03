package com.github.acticfox.base.mybatis.orm.filter.impl;

import java.util.Collection;

import com.github.acticfox.base.mybatis.orm.filter.Filter;
import com.github.acticfox.base.mybatis.orm.filter.enums.CompareOp;
import com.github.acticfox.base.mybatis.orm.filter.enums.Relation;

/**
 * 类FilterFactory.java的实现描述： 创建过滤器的工厂，通过该工厂类将具体的过滤器封装起来。
 * <p>
 * 该工厂会根据需要自动创建相应的过滤器，并为其赋一定的初值， 目前该工厂仅有<code>getSimpleFilter</code>
 * 方法，以后会加入对权限或用户的过滤
 * 
 * @author fanyong.kfy 2015年5月4日 下午2:26:02
 */
public class FilterFactory {

    /**
     * 创建并返回一个普通过滤器
     * 
     * @param name 字段名
     * @param val 值
     * @return 过滤器
     */
    public static Filter getSimpleFilter(String name, Object val) {
        SimpleFilter filter = new SimpleFilter();
        filter.addCondition(name, val);
        return filter;
    }

    /**
     * 创建并返回一个过滤器
     * 
     * @param name 字段名
     * @param val 值
     * @param op 操作符
     * @return 过滤器
     */
    public static Filter getSimpleFilter(String name, Object val, CompareOp op) {
        SimpleFilter filter = new SimpleFilter();
        filter.addCondition(name, val, op);
        return filter;
    }

    /**
     * 创建并返回一个过滤器
     * 
     * @param name 字段名
     * @param val 值
     * @param op 操作符
     * @param relation 关系符
     * @return 过滤器
     */
    public static Filter getSimpleFilter(String name, Object val, CompareOp op, Relation relation) {
        SimpleFilter filter = new SimpleFilter();
        filter.addCondition(name, val, op, relation);
        return filter;
    }

    /**
     * 创建并返回一个带IN操作符的过滤器
     * 
     * @param name 字段名
     * @param val 值
     * @return 过滤器
     */
    public static InFilter getInFilter(String name, Collection val) {
        InFilter filter = new InFilter();
        filter.addCondition(name, val);
        return filter;
    }

    /**
     * 创建并返回一个带IN操作符的过滤器
     * 
     * @param name 字段名
     * @param val 值
     * @param op 操作符
     * @param relation 关系符
     * @return 过滤器 注意:因为接口原因,该方法的op参数无效,无论设置该参数值为什么系统都会将其 自动转换为IN,对应
     * <code>Filter.OPERATOR_IN</code>,见意在调用该方法时将op 操作符设为null.
     * 例如:FilterFactory.getInFilter(name, val, null, relation);
     */
    public static InFilter getInFilter(String name, Collection val, CompareOp op, Relation relation) {
        InFilter filter = new InFilter();
        filter.addCondition(name, val, op, relation);
        return filter;
    }

    /**
     * 创建并返回一个带IN操作符的过滤器
     * 
     * @param name 字段名
     * @param val 值
     * @return 过滤器
     */
    public static NotInFilter getNotInFilter(String name, Collection val) {
        NotInFilter filter = new NotInFilter();
        filter.addCondition(name, val);
        return filter;
    }

    /**
     * 创建并返回一个带IN操作符的过滤器
     * 
     * @param name 字段名
     * @param val 值
     * @param op 操作符
     * @param relation 关系符
     * @return 过滤器 注意:因为接口原因,该方法的op参数无效,无论设置该参数值为什么系统都会将其 自动转换为IN,对应
     * <code>Filter.OPERATOR_IN</code>,见意在调用该方法时将op 操作符设为null.
     * 例如:FilterFactory.getInFilter(name, val, null, relation);
     */
    public static NotInFilter getNotInFilter(String name, Collection val, CompareOp op, Relation relation) {
        NotInFilter filter = new NotInFilter();
        filter.addCondition(name, val, op, relation);
        return filter;
    }

}
