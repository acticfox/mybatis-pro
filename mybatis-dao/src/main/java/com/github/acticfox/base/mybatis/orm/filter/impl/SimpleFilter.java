package com.github.acticfox.base.mybatis.orm.filter.impl;

import com.github.acticfox.base.mybatis.orm.filter.Filter;
import com.github.acticfox.base.mybatis.orm.filter.FilterBean;
import com.github.acticfox.base.mybatis.orm.filter.enums.CompareOp;
import com.github.acticfox.base.mybatis.orm.filter.enums.Relation;

/**
 * 类SimpleFilter.java的实现描述：
 * 
 * <pre>
 * </pre>
 * 
 * @author fanyong.kfy 2015年5月4日 下午2:12:26
 */
public class SimpleFilter extends AbstractFilter {

    private static final long serialVersionUID = 6104162353423716952L;

    public SimpleFilter() {

    }

    @Override
    public Filter addCondition(String name, Object val) {
        return addCondition(name, val, CompareOp.EQ);
    }

    @Override
    public Filter addCondition(String name, Object val, CompareOp op) {
        return addCondition(name, val, op, Relation.AND);
    }

    @Override
    public Filter addCondition(String name, Object val, CompareOp op, Relation relation) {

        if (name == null || name.trim().equals("")) {
            log.warn("addCondition method of name  is null");
            return this; // 如果name为空或空串则当前方法操作无效
        }

        FilterBean condition = new FilterBean();
        if (relation == null)
            relation = Relation.AND;
        // 增加关系符
        condition.setRelations(relation);
        if (op == null) {
            op = CompareOp.EQ;
        }
        // 增加操作符与值
        condition.setOperater(op);
        condition.setValue(val);
        // 设置查询字段的的字段名
        condition.setFieldName(name);
        conditions.add(condition);

        return this;
    }

    @Override
    public Filter addNotNullCondition(String name, Object val) {
        return addNotNullCondition(name, val, CompareOp.EQ);
    }

    @Override
    public Filter addNotNullCondition(String name, Object val, CompareOp op) {
        return addNotNullCondition(name, val, op, Relation.AND);
    }

    @Override
    public Filter addNotNullCondition(String name, Object val, CompareOp op, Relation relation) {
        if (val == null) {
            return this;
        }
        return addCondition(name, val, op, relation);
    }

}
