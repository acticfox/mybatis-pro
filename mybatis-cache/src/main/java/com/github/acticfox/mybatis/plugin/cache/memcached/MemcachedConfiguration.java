/*
 * Copyright 2014 zhichubao.com All right reserved. This software is the
 * confidential and proprietary information of zhichubao.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with zhichubao.com .
 */
package com.github.acticfox.mybatis.plugin.cache.memcached;

import static java.lang.String.format;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.concurrent.TimeUnit;

import net.spy.memcached.ConnectionFactory;

/**
 * 类MemcachedConfiguration.java的实现描述：
 * 
 * <pre>
 * The Memcached client configuration.
 * </pre>
 * 
 * @author fanyong.kfy 2014-7-29 下午7:06:22
 */
final class MemcachedConfiguration {

    /**
     * The key prefix.
     */
    private String                  keyPrefix;

    /**
     * The Connection Factory used to establish the connection to Memcached
     * server(s).
     */
    private ConnectionFactory       connectionFactory;

    /**
     * The Memcached servers.
     */
    private List<InetSocketAddress> addresses;

    /**
     * The flag to switch from sync to async Memcached get.
     */
    private boolean                 usingAsyncGet;

    /**
     * Compression enabled flag.
     */
    private boolean                 compressionEnabled;

    /**
     * The Memcached entries expiration time.
     */
    private int                     expiration;

    /**
     * The Memcached connection timeout when using async get.
     */
    private int                     timeout;

    /**
     * The Memcached timeout unit when using async get.
     */
    private TimeUnit                timeUnit;

    /**
     * @return the keyPrefix
     */
    public String getKeyPrefix() {
        return keyPrefix;
    }

    /**
     * @param keyPrefix the keyPrefix to set
     */
    public void setKeyPrefix(String keyPrefix) {
        this.keyPrefix = keyPrefix;
    }

    /**
     * @return the connectionFactory
     */
    public ConnectionFactory getConnectionFactory() {
        return connectionFactory;
    }

    /**
     * @param connectionFactory the connectionFactory to set
     */
    public void setConnectionFactory(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    /**
     * @return the addresses
     */
    public List<InetSocketAddress> getAddresses() {
        return addresses;
    }

    /**
     * @param addresses the addresses to set
     */
    public void setAddresses(List<InetSocketAddress> addresses) {
        this.addresses = addresses;
    }

    /**
     * @return the usingAsyncGet
     */
    public boolean isUsingAsyncGet() {
        return usingAsyncGet;
    }

    /**
     * @param usingAsyncGet the usingAsyncGet to set
     */
    public void setUsingAsyncGet(boolean usingAsyncGet) {
        this.usingAsyncGet = usingAsyncGet;
    }

    /**
     * @return the compressionEnabled
     */
    public boolean isCompressionEnabled() {
        return compressionEnabled;
    }

    /**
     * @param compressionEnabled the compressionEnabled to set
     */
    public void setCompressionEnabled(boolean compressionEnabled) {
        this.compressionEnabled = compressionEnabled;
    }

    /**
     * @return the expiration
     */
    public int getExpiration() {
        return expiration;
    }

    /**
     * @param expiration the expiration to set
     */
    public void setExpiration(int expiration) {
        this.expiration = expiration;
    }

    /**
     * @return the timeout
     */
    public int getTimeout() {
        return timeout;
    }

    /**
     * @param timeout the timeout to set
     */
    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    /**
     * @return the timeUnit
     */
    public TimeUnit getTimeUnit() {
        return timeUnit;
    }

    /**
     * @param timeUnit the timeUnit to set
     */
    public void setTimeUnit(TimeUnit timeUnit) {
        this.timeUnit = timeUnit;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return hash(1, 31, addresses, compressionEnabled, connectionFactory, expiration, keyPrefix, timeUnit, timeout,
                usingAsyncGet);
    }

    /**
     * Computes a hashCode given the input objects.
     * 
     * @param initialNonZeroOddNumber a non-zero, odd number used as the initial
     *            value.
     * @param multiplierNonZeroOddNumber a non-zero, odd number used as the
     *            multiplier.
     * @param objs the objects to compute hash code.
     * @return the computed hashCode.
     */
    public static int hash(int initialNonZeroOddNumber, int multiplierNonZeroOddNumber, Object... objs) {
        int result = initialNonZeroOddNumber;
        for (Object obj : objs) {
            result = multiplierNonZeroOddNumber * result + (obj != null ? obj.hashCode() : 0);
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        MemcachedConfiguration other = (MemcachedConfiguration) obj;
        return eq(addresses, other.addresses) && eq(compressionEnabled, other.compressionEnabled)
                && eq(connectionFactory, other.connectionFactory) && eq(expiration, other.expiration)
                && eq(keyPrefix, other.keyPrefix) && eq(timeUnit, other.timeUnit) && eq(timeout, other.timeout)
                && eq(usingAsyncGet, other.usingAsyncGet);
    }

    /**
     * Verifies input objects are equal.
     * 
     * @param o1 the first argument to compare
     * @param o2 the second argument to compare
     * @return true, if the input arguments are equal, false otherwise.
     */
    private static <O> boolean eq(O o1, O o2) {
        return o1 != null ? o1.equals(o2) : o2 == null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return format(
                "MemcachedConfiguration [addresses=%s, compressionEnabled=%s, connectionFactory=%s, , expiration=%s, keyPrefix=%s, timeUnit=%s, timeout=%s, usingAsyncGet=%s]",
                addresses, compressionEnabled, connectionFactory, expiration, keyPrefix, timeUnit, timeout,
                usingAsyncGet);
    }

}
