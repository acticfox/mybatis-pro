package com.github.acticfox.base.mybatis.orm.filter.impl;

import com.github.acticfox.base.mybatis.orm.filter.Sorter;
import com.github.acticfox.base.mybatis.orm.filter.enums.SortOrder;

/**
 * 类SorterFactory.java的实现描述：
 * 
 * <pre>
 *  sort 工厂
 * </pre>
 * 
 * @author fanyong.kfy 2015年5月8日 上午11:44:52
 */
public class SorterFactory {

    public static Sorter getSimpleSort(String name) {
        Sorter sort = new SimpleSorter();
        sort.addSort(name);
        return sort;
    }

    public static Sorter getSimpleSort(String name, SortOrder direction) {
        Sorter sort = new SimpleSorter();
        sort.addSort(name, direction);
        return sort;
    }

}
