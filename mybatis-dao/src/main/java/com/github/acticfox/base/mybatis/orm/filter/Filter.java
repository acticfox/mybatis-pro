/*
 * Copyright 2015 zhichubao.com All right reserved. This software is the
 * confidential and proprietary information of zhichubao.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with zhichubao.com .
 */
package com.github.acticfox.base.mybatis.orm.filter;

import java.util.List;
import java.util.Map;

import com.github.acticfox.base.mybatis.orm.FieldDetail;
import com.github.acticfox.base.mybatis.orm.filter.enums.CompareOp;
import com.github.acticfox.base.mybatis.orm.filter.enums.Relation;

/**
 * 类Filter.java的实现描述： 本接口是规范面向对象的查询过滤条件，是对单表的过滤操作
 * <p>
 * 过滤器由四部分组成：字段名、值、操作符与关系符，可以通过<code>addCondition</code>方法
 * 不断累加查询条件，该方法的返回值的还是当前的<code>Filter</code>的实例，所以可以象<code>StringBuffer</code>
 * 一样的操作。
 * <p>
 * 注意:目前的过滤器不支持between操作，但可以通过调用两次<code>addCondition</code>方法来实现
 * 该功能。同时不支持左、右外连接与子查询，可通过相应的ORM在具体的DAO类中实现该功能。
 * <p>
 * 最后，为了规范编码我没有做参<code>addCondition</code>方法做多余的过载，这也就是意味着对
 * 于该方法中的参数val一定要对应相应的POJO中属性的类型
 * 
 * @author fanyong.kfy 2015年5月4日 下午1:55:01
 */
public interface Filter {

    /**
     * 添加筛选条件
     * 
     * @param name 数据库字段名
     * @param val 字段对应的值 注意：默认的操作符如果是字符串为LIKE否则为=<br>
     * 默认的关系符为AND
     * @return 返回当前过滤器
     */
    public Filter addCondition(String name, Object val);

    /**
     * 添加筛选条件
     * 
     * @param name 数据库字段名
     * @param val 字段对应的值
     * @param op 操作符 可以通过Filter.OPERATOR_LIKE等获取操作符的常量 注意：默认的关系符为AND
     * @return 返回当前过滤器
     */
    public Filter addCondition(String name, Object val, CompareOp op);

    /**
     * 添加筛选条件
     * 
     * @param name 数据库字段名
     * @param val 字段对应的值
     * @param op 操作符
     * @param relation 关系符 可以通过Filter.OPERATOR_LIKE等获取操作符的常量
     * Filter.RELATION_AND等获取关系符的常量
     * @return 返回当前过滤器
     */
    public Filter addCondition(String name, Object val, CompareOp op, Relation relation);

    /**
     * 添加筛选条件,如果为空则忽略该条件
     * 
     * @param name 数据库字段名
     * @param val 字段对应的值,不能为空 注意：默认的操作符如果是字符串为LIKE否则为=<br>
     * 默认的关系符为AND
     * @return 返回当前过滤器
     */
    public Filter addNotNullCondition(String name, Object val);

    /**
     * 添加筛选条件,如果为空则忽略该条件
     * 
     * @param name 数据库字段名
     * @param val 字段对应的值,不能为空
     * @param op 操作符 可以通过Filter.OPERATOR_LIKE等获取操作符的常量 注意：默认的关系符为AND
     * @return 返回当前过滤器
     */
    public Filter addNotNullCondition(String name, Object val, CompareOp op);

    /**
     * 添加筛选条件,如果为空则忽略该条件
     * 
     * @param name 数据库字段名
     * @param val 字段对应的值,不能为空
     * @param op 操作符
     * @param relation 关系符 可以通过Filter.OPERATOR_LIKE等获取操作符的常量
     * Filter.RELATION_AND等获取关系符的常量
     * @return 返回当前过滤器
     */
    public Filter addNotNullCondition(String name, Object val, CompareOp op, Relation relation);

    /**
     * 将另一个过滤器添加到当前的过滤器中，两个过滤器间的关系符缺省为AND与关系
     * 
     * @param otherfilter 另一个过滤器
     * @return 返回当前过滤器
     */
    public Filter addFilter(Filter otherfilter);

    /**
     * 将另一个过滤器添加到当前的过滤器中
     * 
     * @param otherfilter 另一个过滤器
     * @param relation 两个过滤器间的关系符
     * @return 返回当前过滤器
     */
    public Filter addFilter(Filter otherFilter, Relation relation);

    /**
     * 获得当前过滤器所有条件的集合，在集合中每个元素都一个<code>ConditionBean</code>
     * <p>
     * 其作用是在<code>Filter</code>与相应的ORM中做数据转换
     * 
     * @return 返回当前过滤器条件集合
     * @see edu.moe.framework.dao.impl.FilterBean
     */
    public List<FilterBean> getConditions();

    public List<List<FilterBean>> getFilterGroup();

    /**
     * 不是普通节点时，返回null
     * 
     * @return 查询表达式
     * @param columns 表的所有列
     */
    public String toWhereSql(Map<String, FieldDetail> columns);

    /**
     * @return 参数名列表
     */
    public Map<String, Object> getParamMap();

    /**
     * 得到SQL语句中Where子句部分的字符串
     * 
     * @return 返回过滤字符串 注意该返回的字符串不包含"where"字符串
     */
    @Override
    public String toString();

}
