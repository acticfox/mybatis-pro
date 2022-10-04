/*
 * Copyright 2014 github.com All right reserved. This software is the
 * confidential and proprietary information of github.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with github.com .
 */
package com.github.acticfox.mybatis.plugin.cache.memcached;

import org.apache.ibatis.cache.decorators.LoggingCache;

/**
 * 类LoggingMemcachedCache.java的实现描述：
 * 
 * <pre>
 * {@code LoggingCache} adapter for Memcached.
 * </pre>
 * 
 * @author fanyong.kfy 2014-7-29 下午7:12:32
 */
public final class LoggingMemcachedCache extends LoggingCache {

    public LoggingMemcachedCache(final String id) {
        super(new MybatisMemcachedCache(id));
    }

}
