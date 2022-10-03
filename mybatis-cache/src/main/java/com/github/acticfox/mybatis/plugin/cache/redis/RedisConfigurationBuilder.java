/*
 * Copyright 2022 cecloud.com All right reserved. This software is the confidential and proprietary information of
 * cecloud.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only
 * in accordance with the terms of the license agreement you entered into with cecloud.com .
 */
package com.github.acticfox.mybatis.plugin.cache.redis;

import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.cache.CacheException;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.core.io.ClassPathResource;

/**
 * @Description: TODO
 * @author kfy May 21, 2022 6:02:49 PM
 * @version V1.0
 */
public final class RedisConfigurationBuilder {

    private static final Logger log = LoggerFactory.getLogger(RedisConfigurationBuilder.class);
    /**
     * This class instance.
     */
    private static final RedisConfigurationBuilder INSTANCE = new RedisConfigurationBuilder();

    private static final String SYSTEM_PROPERTY_REDIS_PROPERTIES_FILENAME = "redis.properties.filename";

    private static final String REDIS_RESOURCE = "application.yml";

    private static final String propertyPrefix = "mybatis.redis.";

    private final String redisPropertiesFilename;

    /**
     * Hidden constructor, this class can't be instantiated.
     */
    private RedisConfigurationBuilder() {
        redisPropertiesFilename = System.getProperty(SYSTEM_PROPERTY_REDIS_PROPERTIES_FILENAME, REDIS_RESOURCE);
    }

    /**
     * Return this class instance.
     *
     * @return this class instance.
     */
    public static RedisConfigurationBuilder getInstance() {
        return INSTANCE;
    }

    /**
     * Parses the Config and builds a new {@link RedisConfig}.
     *
     * @return the converted {@link RedisConfig}.
     */
    public RedisConfig parseConfiguration() {
        return parseConfiguration(getClass().getClassLoader());
    }

    /**
     * Parses the Config and builds a new {@link RedisConfig}.
     *
     * @param the {@link ClassLoader} used to load the {@code memcached.properties} file in classpath.
     * @return the converted {@link RedisConfig}.
     */
    public RedisConfig parseConfiguration(ClassLoader classLoader) {
        YamlPropertiesFactoryBean factoryBean = new YamlPropertiesFactoryBean();
        factoryBean.setResources(new ClassPathResource(redisPropertiesFilename));
        Properties config = factoryBean.getObject();

        RedisConfig jedisConfig = new RedisConfig();
        setConfigProperties(config, jedisConfig);
        return jedisConfig;
    }

    private void setConfigProperties(Properties properties, RedisConfig jedisConfig) {
        if (properties == null) {
            return;
        }
        MetaObject metaCache = SystemMetaObject.forObject(jedisConfig);
        for (Map.Entry<Object, Object> entry : properties.entrySet()) {
            String name = (String)entry.getKey();
            if (!StringUtils.startsWith(name, propertyPrefix)) {
                continue;
            }
            name = StringUtils.substringAfter(name, propertyPrefix);
            String configValue = String.valueOf(entry.getValue());
            String value = null;
            if (StringUtils.startsWith(configValue, "${") && StringUtils.endsWith(configValue, "}")) {
                String envKey = StringUtils.substringBetween(configValue, "${", ":");
                value = System.getenv(envKey);
                if (StringUtils.isBlank(value)) {
                    value = StringUtils.substringBetween(configValue, ":", "}");
                }
            } else {
                value = configValue;
            }
            if (metaCache.hasSetter(name)) {
                Class<?> type = metaCache.getSetterType(name);
                if (String.class == type) {
                    metaCache.setValue(name, value);
                } else if (int.class == type || Integer.class == type) {
                    metaCache.setValue(name, Integer.valueOf(value));
                } else if (long.class == type || Long.class == type) {
                    metaCache.setValue(name, Long.valueOf(value));
                } else if (short.class == type || Short.class == type) {
                    metaCache.setValue(name, Short.valueOf(value));
                } else if (byte.class == type || Byte.class == type) {
                    metaCache.setValue(name, Byte.valueOf(value));
                } else if (float.class == type || Float.class == type) {
                    metaCache.setValue(name, Float.valueOf(value));
                } else if (boolean.class == type || Boolean.class == type) {
                    metaCache.setValue(name, Boolean.valueOf(value));
                } else if (double.class == type || Double.class == type) {
                    metaCache.setValue(name, Double.valueOf(value));
                } else {
                    throw new CacheException("Unsupported property type: '" + name + "' of type " + type);
                }
            }
        }
    }

}
