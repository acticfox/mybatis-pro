/*
 * Copyright 2014 zhichubao.com All right reserved. This software is the
 * confidential and proprietary information of zhichubao.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with zhichubao.com .
 */
package com.github.acticfox.mybatis.plugin.cache.memcached;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

import net.spy.memcached.AddrUtil;
import net.spy.memcached.ConnectionFactory;
import net.spy.memcached.DefaultConnectionFactory;

/**
 * 类MemcachedConfigurationBuilder.java的实现描述：
 * 
 * <pre>
 * Converter from the Config to a proper {@link MemcachedConfiguration}.
 * </pre>
 * 
 * @author fanyong.kfy 2014-7-29 下午7:05:30
 */
final class MemcachedConfigurationBuilder {

	/**
	 * This class instance.
	 */
	private static final MemcachedConfigurationBuilder INSTANCE = new MemcachedConfigurationBuilder();

	private static final String SYSTEM_PROPERTY_MEMCACHED_PROPERTIES_FILENAME = "memcached.properties.filename";

	/**
	 *
	 */
	private static final String MEMCACHED_RESOURCE = "application.properties";

	private final String memcachedPropertiesFilename;

	/**
	 * Return this class instance.
	 * 
	 * @return this class instance.
	 */
	public static MemcachedConfigurationBuilder getInstance() {
		return INSTANCE;
	}

	/**
	 * Hidden constructor, this class can't be instantiated.
	 */
	private MemcachedConfigurationBuilder() {
		memcachedPropertiesFilename = System.getProperty(SYSTEM_PROPERTY_MEMCACHED_PROPERTIES_FILENAME,
				MEMCACHED_RESOURCE);

	}

	/**
	 * Parses the Config and builds a new {@link MemcachedConfiguration}.
	 * 
	 * @return the converted {@link MemcachedConfiguration}.
	 */
	public MemcachedConfiguration parseConfiguration() {
		Configuration config = null;
		try {
			config = new PropertiesConfiguration(memcachedPropertiesFilename);
		} catch (ConfigurationException e) {
			throw new RuntimeException("memcached config file not found");
		}

		MemcachedConfiguration memcachedConfiguration = new MemcachedConfiguration();
		memcachedConfiguration.setCompressionEnabled(config.getBoolean("mybatis.memcached.compression", true));
		memcachedConfiguration.setUsingAsyncGet(config.getBoolean("mybatis.memcached.asyncget", false));
		memcachedConfiguration.setTimeout(config.getInt("mybatis.memcached.timeout", 5));
		memcachedConfiguration.setExpiration(config.getInt("mybatis.memcached.expiration", 60 * 60 * 24 * 30));
		memcachedConfiguration.setKeyPrefix(config.getString("mybatis.memcached.keyPrefix", "_mybatis_"));
		memcachedConfiguration.setAddresses(AddrUtil.getAddresses(config.getString("mybatis.memcached.servers")));

		ConnectionFactory connectionFactory = null;
		String conFacClz = config.getString("memcached.connectionfactory");
		if (org.apache.commons.lang.StringUtils.isBlank(conFacClz)) {
			connectionFactory = new DefaultConnectionFactory();
		} else {

			Class<?> clazz;
			try {
				clazz = Class.forName(conFacClz);
			} catch (ClassNotFoundException e) {
				throw new RuntimeException("memcached connectionfactory class not found", e);
			}
			if (!ConnectionFactory.class.isAssignableFrom(clazz)) {
				throw new IllegalArgumentException("Class '" + clazz.getName() + "' is not a valid '"
						+ ConnectionFactory.class.getName() + "' implementation");
			}
			try {
				connectionFactory = (ConnectionFactory) clazz.newInstance();
			} catch (InstantiationException e) {
				throw new RuntimeException("memcached connectionfactory init failed InstantiationException", e);
			} catch (IllegalAccessException e) {
				throw new RuntimeException("memcached connectionfactory init failed IllegalAccessException", e);
			}
		}
		memcachedConfiguration.setConnectionFactory(connectionFactory);

		return memcachedConfiguration;
	}

}
