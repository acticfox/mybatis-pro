/*
 * Copyright 2015 github.com All right reserved. This software is the
 * confidential and proprietary information of github.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with github.com .
 */
package com.github.acticfox.base.mybatis.orm.filter;

import java.util.Map;

import com.github.acticfox.base.mybatis.orm.FieldDetail;
import com.github.acticfox.base.mybatis.orm.filter.enums.SortOrder;

/**
 * 类Sorter.java的实现描述：
 * <p>
 * 本接口是规范面向对象的查询排序条件,排序分为两部分：字段名、方向符，可以通过<code>addSort</code>方法不断累加排序方法返回的还是 当前
 * <code>Sorter</code>的实例，其操作方式与<code>Filter</code>方式相同 本接口是规范以面向对象的方式对数据库查询进行排序
 * </p>
 * 
 * @author fanyong.kfy 2015年5月4日 下午1:56:41
 */
public interface Sorter {

    /**
     * 添加排序条件
     * 
     * @param name 数据库字段名 注意：默认的排序方向为正序，即ASC
     * @return 返回当前的排序器
     */

    public Sorter addSort(String name);

    /**
     * 添加排序条件
     * 
     * @param name 数据库字段名
     * @param direction 排序方向 可以通过Sorter.ORDER_ASC等获取排序方向的常量
     * @return 返回当前的排序器
     */
    public Sorter addSort(String name, SortOrder direction);

    /**
     * 获得当前排序器的一个Map,其中key:字段名，value：排序的方向
     * <p>
     * 其作用是Sorter与相应的ORM中做数据转换
     * 
     * @return 返回排序信息的集合
     */
    public Map<String, String> getSorts();

    /**
     * 得到SQL语句中Order by子句部分的字符串
     * 
     * @return 返回过滤字符串 注意该返回的字符串不包含"order by"字符串
     */
    public String toSortSql(Map<String, FieldDetail> map);

}
