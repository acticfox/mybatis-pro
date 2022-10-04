/*
 * Copyright 2014 github.com All right reserved. This software is the
 * confidential and proprietary information of github.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with github.com .
 */
package com.github.acticfox.mybatis.plugin.cache.memcached;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Future;

import net.spy.memcached.MemcachedClient;
import org.apache.ibatis.cache.CacheException;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;

import com.github.acticfox.mybatis.plugin.common.CompressorTranscoder;
import com.github.acticfox.mybatis.plugin.common.StringUtils;

/**
 * 类MemcachedClientWrapper.java的实现描述：
 * 
 * <pre>
 * MemcachedClient包装器
 * </pre>
 * 
 * @author fanyong.kfy 2014-7-29 下午7:11:50
 */
final class MemcachedClientWrapper {

    /**
     * This class log.
     */
    private final Log log = LogFactory.getLog(MybatisMemcachedCache.class);

    private final MemcachedConfiguration configuration;

    private final MemcachedClient client;

    public MemcachedClientWrapper() {
        configuration = MemcachedConfigurationBuilder.getInstance().parseConfiguration();
        try {
            client = new MemcachedClient(configuration.getConnectionFactory(), configuration.getAddresses());
        } catch (IOException e) {
            String message = "Impossible to instantiate a new memecached client instance, see nested exceptions";
            log.error(message, e);
            throw new RuntimeException(message, e);
        }

        if (log.isDebugEnabled()) {
            log.debug("Running new Memcached client using " + configuration);
        }
    }

    /**
     * Converts the MyBatis object key in the proper string representation.
     * 
     * @param key the MyBatis object key.
     * @return the proper string representation.
     */
    private String toKeyString(final Object key) {
        String keyString = configuration.getKeyPrefix() + StringUtils.sha1Hex(key.toString()); // issue #1, key too long
        if (log.isDebugEnabled()) {
            log.debug("Object key '" + key + "' converted in '" + keyString + "'");
        }
        return keyString;
    }

    /**
     * @param key
     * @return
     */
    public Object getObject(Object key) {
        String keyString = toKeyString(key);
        Object ret = retrieve(keyString);

        if (log.isDebugEnabled()) {
            log.debug("Retrived object (" + keyString + ", " + ret + ")");
        }

        return ret;
    }

    /**
     * Return the stored group in Memcached identified by the specified key.
     * 
     * @param groupKey the group key.
     * @return the group if was previously stored, null otherwise.
     */
    @SuppressWarnings("unchecked")
    private Set<String> getGroup(String groupKey) {
        if (log.isDebugEnabled()) {
            log.debug("Retrieving group with id '" + groupKey + "'");
        }

        Object groups = null;
        try {
            groups = retrieve(groupKey);
        } catch (Exception e) {
            log.error("Impossible to retrieve group '" + groupKey + "' see nested exceptions", e);
        }

        if (groups == null) {
            if (log.isDebugEnabled()) {
                log.debug("Group '" + groupKey + "' not previously stored");
            }
            return null;
        }

        if (log.isDebugEnabled()) {
            log.debug("retrieved group '" + groupKey + "' with values " + groups);
        }
        return (Set<String>)groups;
    }

    /**
     * @param keyString
     * @return
     * @throws Exception
     */
    private Object retrieve(final String keyString) {
        Object retrieved = null;

        if (configuration.isUsingAsyncGet()) {
            Future<Object> future;
            if (configuration.isCompressionEnabled()) {
                future = client.asyncGet(keyString, new CompressorTranscoder());
            } else {
                future = client.asyncGet(keyString);
            }

            try {
                retrieved = future.get(configuration.getTimeout(), configuration.getTimeUnit());
            } catch (Exception e) {
                future.cancel(false);
                throw new CacheException(e);
            }
        } else {
            if (configuration.isCompressionEnabled()) {
                retrieved = client.get(keyString, new CompressorTranscoder());
            } else {
                retrieved = client.get(keyString);
            }
        }

        return retrieved;
    }

    public void putObject(Object key, Object value, String id) {
        String keyString = toKeyString(key);
        String groupKey = toKeyString(id);

        if (log.isDebugEnabled()) {
            log.debug("Putting object (" + keyString + ", " + value + ")");
        }

        storeInMemcached(keyString, value);

        // add namespace key into memcached
        Set<String> group = getGroup(groupKey);
        if (group == null) {
            group = new HashSet<String>();
        }
        group.add(keyString);

        if (log.isDebugEnabled()) {
            log.debug("Insert/Updating object (" + groupKey + ", " + group + ")");
        }

        storeInMemcached(groupKey, group);
    }

    /**
     * Stores an object identified by a key in Memcached.
     * 
     * @param keyString the object key
     * @param value the object has to be stored.
     */
    private void storeInMemcached(String keyString, Object value) {
        if (value != null && !Serializable.class.isAssignableFrom(value.getClass())) {
            throw new CacheException("Object of type '" + value.getClass().getName()
                + "' that's non-serializable is not supported by Memcached");
        }

        if (configuration.isCompressionEnabled()) {
            client.set(keyString, configuration.getExpiration(), value, new CompressorTranscoder());
        } else {
            client.set(keyString, configuration.getExpiration(), value);
        }
    }

    public Object removeObject(Object key) {
        String keyString = toKeyString(key);

        if (log.isDebugEnabled()) {
            log.debug("Removing object '" + keyString + "'");
        }

        Object result = getObject(key);
        if (result != null) {
            client.delete(keyString);
        }
        return result;
    }

    public void removeGroup(String id) {
        String groupKey = toKeyString(id);

        Set<String> group = getGroup(groupKey);

        if (group == null) {
            if (log.isDebugEnabled()) {
                log.debug("No need to flush cached entries for group '" + id + "' because is empty");
            }
            return;
        }

        if (log.isDebugEnabled()) {
            log.debug("Flushing keys: " + group);
        }

        for (String key : group) {
            client.delete(key);
        }

        if (log.isDebugEnabled()) {
            log.debug("Flushing group: " + groupKey);
        }

        client.delete(groupKey);
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        client.shutdown(configuration.getTimeout(), configuration.getTimeUnit());
    }

}
