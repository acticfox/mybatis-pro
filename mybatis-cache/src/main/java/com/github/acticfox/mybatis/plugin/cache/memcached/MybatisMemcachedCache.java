/*
 * Copyright 2014 zhichubao.com All right reserved. This software is the
 * confidential and proprietary information of zhichubao.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with zhichubao.com .
 */
package com.github.acticfox.mybatis.plugin.cache.memcached;

import java.util.concurrent.locks.ReadWriteLock;

import org.apache.ibatis.cache.Cache;

import com.github.acticfox.mybatis.plugin.common.DummyReadWriteLock;

/**
 * 类MemcachedCache.java的实现描述：
 * 
 * <pre>
 * The Memcached-based Cache implementation.
 * </pre>
 * 
 * @author fanyong.kfy 2014-7-29 下午7:12:17
 */
public final class MybatisMemcachedCache implements Cache {

    private static final MemcachedClientWrapper MEMCACHED_CLIENT = new MemcachedClientWrapper();

    /**
     * The {@link ReadWriteLock}.
     */
    private final ReadWriteLock readWriteLock = new DummyReadWriteLock();

    /**
     * The cache id.
     */
    private final String id;

    /**
     * Builds a new Memcached-based Cache.
     * 
     * @param id the Mapper id.
     */
    public MybatisMemcachedCache(final String id) {
        this.id = id;
    }

    /**
     * {@inheritDoc}
     */
    public void clear() {
        MEMCACHED_CLIENT.removeGroup(this.id);
    }

    /**
     * {@inheritDoc}
     */
    public String getId() {
        return this.id;
    }

    /**
     * {@inheritDoc}
     */
    public Object getObject(Object key) {
        return MEMCACHED_CLIENT.getObject(key);
    }

    /**
     * {@inheritDoc}
     */
    public ReadWriteLock getReadWriteLock() {
        return this.readWriteLock;
    }

    /**
     * {@inheritDoc}
     */
    public int getSize() {
        return Integer.MAX_VALUE;
    }

    /**
     * {@inheritDoc}
     */
    public void putObject(Object key, Object value) {
        MEMCACHED_CLIENT.putObject(key, value, this.id);
    }

    /**
     * {@inheritDoc}
     */
    public Object removeObject(Object key) {
        return MEMCACHED_CLIENT.removeObject(key);
    }

}
