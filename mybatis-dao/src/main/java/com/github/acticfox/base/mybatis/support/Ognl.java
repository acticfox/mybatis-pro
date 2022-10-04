package com.github.acticfox.base.mybatis.support;
/*
 * Copyright 2014 github.com All right reserved. This software is the
 * confidential and proprietary information of github.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with github.com .
 */


import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

/**
  * Ognl工具类，主要是为了在Ognl表达式访问静态方法时可以减少长长的类名称编写
 * Ognl访问静态方法的表达式为: @class@method(args)
 * 
 * 示例使用: 
 * <pre>
 *      &lt;if test="@Ognl@isNotEmpty(userId)">
 *              and user_id = #{userId}
 *      &lt;/if>
 * </pre>
 * 
 * @author fanyong.kfy 2014-1-22 上午11:51:24
 */
public class Ognl {

    /**
     * 可以用于判断String,Map,Collection,Array是否为空
     * 
     * @param o
     * @return
     */
    public static boolean isEmpty(Object o) throws IllegalArgumentException {
        if (o == null)
            return true;

        if (o instanceof String) {
            if (((String) o).length() == 0) {
                return true;
            }
        } else if (o instanceof Collection) {
            if (((Collection) o).isEmpty()) {
                return true;
            }
        } else if (o.getClass().isArray()) {
            if (Array.getLength(o) == 0) {
                return true;
            }
        } else if (o instanceof Map) {
            if (((Map) o).isEmpty()) {
                return true;
            }
        } else {
            return false;
        }

        return false;
    }

    /**
     * 可以用于判断 Map,Collection,String,Array是否不为空
     * 
     * @param c
     * @return
     */
    public static boolean isNotEmpty(Object o) {
        return !isEmpty(o);
    }

    public static boolean isNotBlank(Object o) {
        return !isBlank(o);
    }

    public static boolean isNumber(Object o) {
        if (o == null)
            return false;
        if (o instanceof Number) {
            return true;
        }
        if (o instanceof String) {
            return org.apache.commons.lang.StringUtils.isNumeric((String) o);
        }

        return false;
    }

    public static boolean isBlank(Object o) {
        if (o == null)
            return true;
        if (o instanceof String) {
            return StringUtils.isBlank((String) o);
        }

        return false;
    }

}
