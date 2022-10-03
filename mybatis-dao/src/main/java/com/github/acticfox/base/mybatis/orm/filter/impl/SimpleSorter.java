package com.github.acticfox.base.mybatis.orm.filter.impl;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.github.acticfox.base.mybatis.orm.FieldDetail;
import com.github.acticfox.base.mybatis.orm.filter.Sorter;
import com.github.acticfox.base.mybatis.orm.filter.enums.SortOrder;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 类SimpleSorter.java的实现描述：
 * 
 * <pre>
 * </pre>
 * 
 * @author fanyong.kfy 2015年5月4日 下午2:24:55
 */
public class SimpleSorter implements Sorter {

    protected final Logger log = LoggerFactory.getLogger(SimpleSorter.class);

    /**
     * 排序集合，其作用是Sorter与相应的ORM中做数据转换
     */
    private Map<String, String> sorts = new LinkedHashMap<String, String>();

    /*
     * (non-Javadoc)
     * @see org.hi.framework.dao.Sorter#addSort(java.lang.String)
     */
    @Override
    public Sorter addSort(String name) {
        if (name == null || name.trim().equals("")) {
            log.warn("addSort method of name is null");
            return this; // 如果name为空或空串则当前方法操作无效
        }

        return addSort(name, SortOrder.ASC);
    }

    /*
     * (non-Javadoc)
     * @see org.hi.framework.dao.Sorter#addSort(java.lang.String,
     * java.lang.String)
     */
    @Override
    public Sorter addSort(String name, SortOrder direction) {
        if (name == null || name.trim().equals("")) {
            log.warn("addSort method of name is null");
            return this; // 如果name为空或空串则当前方法操作无效
        }

        if (direction == null)
            direction = SortOrder.ASC;
        sorts.put(name, direction.getOp());

        return this;
    }

    @Override
    public String toSortSql(Map<String, FieldDetail> cloumnMap) {
        List<String> sotrList = Lists.newArrayList();
        for (Map.Entry<String, String> entry : sorts.entrySet()) {
            String fieldName = entry.getKey();
            String direction = entry.getValue();
            if (!cloumnMap.containsKey(fieldName)) {
                continue;
            }
            sotrList.add(String.format("%s %s", cloumnMap.get(fieldName).getColumnName(), direction));
        }

        return Joiner.on(",").join(sotrList);
    }

    /*
     * (non-Javadoc)
     * @see org.hi.framework.dao.Sorter#getSorts()
     */
    @Override
    public Map<String, String> getSorts() {
        return sorts;
    }

}
