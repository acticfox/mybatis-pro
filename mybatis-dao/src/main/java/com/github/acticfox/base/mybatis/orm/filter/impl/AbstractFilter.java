/*
 * Copyright 2015 zhichubao.com All right reserved. This software is the
 * confidential and proprietary information of zhichubao.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with zhichubao.com .
 */
package com.github.acticfox.base.mybatis.orm.filter.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.github.acticfox.base.mybatis.orm.FieldDetail;
import com.github.acticfox.base.mybatis.orm.filter.Filter;
import com.github.acticfox.base.mybatis.orm.filter.FilterBean;
import com.github.acticfox.base.mybatis.orm.filter.enums.CompareOp;
import com.github.acticfox.base.mybatis.orm.filter.enums.Relation;
import com.google.common.collect.Maps;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

/**
 * 类AbstractFilter.java的实现描述：
 * 
 * <pre>
 * </pre>
 * 
 * @author fanyong.kfy 2015年5月4日 下午2:03:14
 */
public abstract class AbstractFilter implements Filter {

    protected final Logger log = LoggerFactory.getLogger(getClass());

    /**
     * 其作用是在<code>Filter</code>与相应的ORM中做数据转换
     */
    protected List<FilterBean> conditions = new ArrayList<FilterBean>();

    protected List<List<FilterBean>> filterGroup = new ArrayList<List<FilterBean>>();

    /**
     * 
     */
    private Map<String, Object> paramMap = Maps.newConcurrentMap();

   
    @Override
    public Filter addFilter(Filter otherFilter) {
        return addFilter(otherFilter, Relation.AND);
    }

   
    @Override
    public Filter addFilter(Filter otherFilter, Relation relation) {
        if (otherFilter == null || otherFilter.getConditions() == null || otherFilter.getConditions().size() < 1)
            return this;

        if (relation == null)
            relation = Relation.AND;

        List<FilterBean> conditions = otherFilter.getConditions();
        if (filterGroup.size() == 0 && this.conditions.size() != 0) {
            List<FilterBean> mainConditions = new ArrayList<FilterBean>();
            mainConditions.addAll(this.conditions);
            filterGroup.add(mainConditions);
        }

        List<List<FilterBean>> otherGroup = otherFilter.getFilterGroup();
        if (otherGroup.size() > 1)
            for (List<FilterBean> group : otherGroup) {
                filterGroup.add(group);
            }
        else
            filterGroup.add(conditions);

        for (int i = 0; i < conditions.size(); i++) {
            FilterBean filterBean = conditions.get(i);
            if (i == 0)
                filterBean.setRelations(relation);
            this.conditions.add(filterBean);
        }

        return this;
    }

    @Override
    public String toWhereSql(Map<String, FieldDetail> columns) {
        List<FilterBean> filterBeanList = getConditions();
        List<List<FilterBean>> filterGroupList = getFilterGroup();
        StringBuilder mainSb = new StringBuilder();
        if (filterGroupList.size() <= 1) {
            buliderSql(mainSb, filterBeanList, null, columns);
        } else {
            for (int i = 0; i < filterGroup.size(); i++) {
                List<FilterBean> filterBeans = filterGroup.get(i);
                if (CollectionUtils.isEmpty(filterBeanList)) {
                    continue;
                }
                buliderSql(mainSb, filterBeans, i, columns);
                if (i > 0) {
                    mainSb.append(") ");
                }
            }
        }
        log.debug("create sql:{}", mainSb.toString());

        return mainSb.toString();
    }

    private void buliderSql(StringBuilder mainSb, List<FilterBean> filterBeans, Integer groupIndex,
        Map<String, FieldDetail> columns) {
        for (int i = 0; i < filterBeans.size(); i++) {
            FilterBean filterBean = filterBeans.get(i);
            CompareOp operater = filterBean.getOperater();

            FieldDetail fieldDetail = columns.get(filterBean.getFieldName());
            if (fieldDetail == null) {
                throw new IllegalArgumentException("列名称:[" + filterBean.getFieldName() + "]不存在,请检测查询条件");
            }
            String columnName = fieldDetail.getColumnName();
            String paramName = filterBean.getFieldName() + "_" + String.valueOf(filterBean.getValue()).hashCode();
            String paramNameWithWrap = fieldDetail.getParamNameWithWrap(paramName);
            String paramNameWithoutWrap = fieldDetail.getParamNameWithoutWrap(paramName);

            if (groupIndex != null && groupIndex != 0 && i == 0) // 如果不是第一个条件组,则在关系符后面加括号
                mainSb.append(" ").append(filterBean.getRelations()).append(" ( ");

            if (i > 0)
                mainSb.append(" ").append(filterBean.getRelations()).append(" "); // 添加两个条件之间的关系符

            Object value = filterBean.getValue();
            CompareOp op = filterBean.getOperater();
            if (op == CompareOp.IsNull) {
                mainSb.append(String.format("`%s` IS NULL", columnName));
            } else if (op == CompareOp.IsNotNull) {
                mainSb.append(String.format("`%s` IS NOT NULL", columnName));
            } else if (value == null) {
                // NULL, 查询为NULL字段
                mainSb.append(String.format("`%s` IS NULL", columnName));
            } else if (op == CompareOp.Like || op == CompareOp.LeftLike || op == CompareOp.RightLike) {
                // Like
                StringBuilder valSb = new StringBuilder();
                if (op == CompareOp.Like)
                    valSb.append("%").append(value).append("%");
                else if (op == CompareOp.LeftLike)
                    valSb.append("%").append(value);
                else if (op == CompareOp.RightLike)
                    valSb.append(value).append("%");

                mainSb.append(String.format("`%s` %s %s", columnName, op.getOpStr(), paramNameWithoutWrap));
                paramMap.put(paramName, valSb.toString());

            } else if (op == CompareOp.NotLike || op == CompareOp.NotLeftLike || op == CompareOp.NotRightLike) {
                // Not Like
                StringBuilder valSb = new StringBuilder();
                if (op == CompareOp.NotLike)
                    valSb.append("%").append(value).append("%");
                else if (op == CompareOp.NotLeftLike)
                    valSb.append("%").append(value);
                else if (op == CompareOp.NotRightLike)
                    valSb.append(value).append("%");

                mainSb.append(String.format("(`%s` IS NULL or `%s` %s %s)", columnName, columnName, op.getOpStr(),
                    paramNameWithoutWrap));
                paramMap.put(paramName, value);
            } else if (operater == CompareOp.IN || operater == CompareOp.NOT_IN) {
                List<String> params = new ArrayList<String>();
                Collection coll = (Collection)filterBean.getValue();
                for (Object inVal : coll) {
                    if (inVal == null) {
                        continue;
                    }
                    paramName = filterBean.getFieldName() + "_" + String.valueOf(inVal).hashCode();
                    params.add(fieldDetail.getParamNameWithWrap(paramName));
                    paramMap.put(paramName, inVal);
                }
                mainSb.append(String.format("`%s` %s (%s)", fieldDetail.getColumnName(), operater.getOpStr(),
                    StringUtils.join(params, ",")));
            } else {
                // Others
                mainSb.append(String.format("`%s` %s %s", columnName, op.getOpStr(), paramNameWithWrap));
                paramMap.put(paramName, value);
            }

        }
    }

    @Override
    public Map<String, Object> getParamMap() {

        return this.paramMap;
    }

    @Override
    public List<FilterBean> getConditions() {
        return conditions;
    }

    @Override
    public List<List<FilterBean>> getFilterGroup() {
        return filterGroup;
    }

    @Override
    public String toString() {
        StringBuffer strBuf = new StringBuffer();
        for (List<FilterBean> group : filterGroup) {
            if (filterGroup.size() > 1)
                strBuf.append("[");
            for (FilterBean filterBean : group) {
                strBuf.append("[");
                strBuf.append("name:").append(filterBean.getFieldName()).append(",");
                strBuf.append("operater:").append(filterBean.getOperater()).append(",");
                strBuf.append("value:").append(filterBean.getValue().toString()).append(",");
                strBuf.append("realtion:").append(filterBean.getRelations()).append(",");
                strBuf.append("not:").append(filterBean.isNot());
                strBuf.append("]");
            }
            if (filterGroup.size() > 1)
                strBuf.append("]");

        }

        return strBuf.toString();
    }

}
