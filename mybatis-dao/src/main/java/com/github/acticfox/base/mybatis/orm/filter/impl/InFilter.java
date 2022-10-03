package com.github.acticfox.base.mybatis.orm.filter.impl;

import java.util.Collection;

import org.springframework.util.CollectionUtils;

import com.github.acticfox.base.mybatis.orm.filter.Filter;
import com.github.acticfox.base.mybatis.orm.filter.FilterBean;
import com.github.acticfox.base.mybatis.orm.filter.enums.CompareOp;
import com.github.acticfox.base.mybatis.orm.filter.enums.Relation;

/**
 * 类InFilter.java的实现描述：
 * <p>
 * 该过滤器是为了满足SQL语句中的IN子句所做的特殊过滤器类 正因为于此,所以该类中的成员方法 <code>addCondition(...)</code>
 * 所有与操作符对应的 参数都将是无效的,系统会其自动转换为 <code>Filter.OPERATOR_IN</code>值
 * 
 * @author fanyong.kfy 2015年5月4日 下午2:16:37
 */
public class InFilter extends AbstractFilter {

    private static final long serialVersionUID = 3988800758696951324L;

    InFilter() {

    }

    /**
     * 添加筛选条件
     * 
     * @param name 数据库字段名
     * @param val 字段对应的值，类型必须是<code>Collection</code>的子类
     * @return 返回当前过滤器
     */
    @Override
    public InFilter addCondition(String name, Object val) {
        return addCondition(name, val, CompareOp.IN, Relation.AND);
    }

    @Override
    @Deprecated
    public InFilter addCondition(String name, Object val, CompareOp op) {
        return addCondition(name, val, CompareOp.IN, Relation.AND);
    }

    /**
     * 添加筛选条件
     * 
     * @param name 数据库字段名
     * @param val 字段对应的值，类型必须是<code>Collection</code>的子类
     * @param op 操作符
     * @param relation 关系符 注意：在本方法只参数op(操作符)无论设为何值,系统都将其改写为IN的特定操作符,甚至是null
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
            conditions.add(condition); // 将封装好的FilterBean存放到集合中
        } else {
            // 如果name为空或空串则当前方法操作无效
            log.warn("this is filter value not Collection Class");
            return this;
        }

        return this;
    }

    @Override
    public Filter addNotNullCondition(String name, Object val) {
        return addNotNullCondition(name, val, CompareOp.IN, Relation.AND);
    }

    @Override
    @Deprecated
    public Filter addNotNullCondition(String name, Object val, CompareOp op) {
        return addNotNullCondition(name, val, CompareOp.IN, Relation.AND);
    }

    @Override
    public Filter addNotNullCondition(String name, Object val, CompareOp op, Relation relation) {
        if (val == null || CollectionUtils.isEmpty((Collection)val)) {
            throw new IllegalArgumentException("列名:[" + name + "]对应值不能为空");
        }
        return addNotNullCondition(name, val, CompareOp.IN, Relation.AND);
    }
}
