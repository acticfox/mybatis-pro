package com.github.acticfox.base.mybatis.orm.filter.impl;

import java.util.Collection;

import com.github.acticfox.base.mybatis.orm.filter.FilterBean;
import com.github.acticfox.base.mybatis.orm.filter.enums.CompareOp;
import com.github.acticfox.base.mybatis.orm.filter.enums.Relation;

/**
 * 类NotInFilter.java的实现描述：
 * 
 * <pre>
 * </pre>
 * 
 * @author fanyong.kfy 2015年5月4日 下午2:19:39
 */
public class NotInFilter extends InFilter {

    public NotInFilter() {

    }

    /**
     * 添加筛选条件
     * 
     * @param name 数据库字段名
     * @param val 字段对应的值，类型必须是<code>Collection</code>的子类
     * @param op 操作符
     * @param relation 关系符 注意：在本方法只参数op(操作符)无论设为何值,系统都将其改写为IN的特定操作符,甚至是null
     * <p>
     * <code>Filter.OPERATOR_IN</code>
     * @return 返回当前过滤器
     */
    @Override
    public InFilter addCondition(String name, Object val, CompareOp op, Relation relation) {
        op = CompareOp.IN;
        if (name == null || name.trim().equals("") || val == null) {
            log.warn("addCondition method of name or value is null");
            return this; // 如果name为空或空串则当前方法操作无效
        }

        if (val instanceof Collection) {
            Collection vals = (Collection)val;
            if (vals.size() == 0)
                return this;
            FilterBean condition = new FilterBean();
            if (relation == null)
                relation = Relation.AND;
            condition.setRelations(relation); // 增加关系符
            condition.setFieldName(name);
            condition.setOperater(op); // 添加操作符
            condition.setValue(val);
            condition.setNot(true);
            conditions.add(condition); // 将封装好的FilterBean存放到集合中
        } else {
            log.warn("this is filter value not Collection Class");
            return this; // 如果name为空或空串则当前方法操作无效
        }

        return this;
    }
}
