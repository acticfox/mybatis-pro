/*
 * Copyright 2022 cecloud.com All right reserved. This software is the
 * confidential and proprietary information of cecloud.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with cecloud.com .
 */
package com.github.acticfox.mybatis.plugin.cache.redis;

import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Protocol;

/** 
 * @Description: TODO
 * @author kfy May 21, 2022 6:13:32 PM
 * @version V1.0  
 */
public class RedisConfig extends JedisPoolConfig {

	/**
	* The key prefix.
	*/
	private String keyPrefix = "_mybatis_0522";
	private String host = Protocol.DEFAULT_HOST;
	private int port = Protocol.DEFAULT_PORT;
	private int connectionTimeout = Protocol.DEFAULT_TIMEOUT;
	private int soTimeout = Protocol.DEFAULT_TIMEOUT;
	private String password;
	private int database = Protocol.DEFAULT_DATABASE;
	private String clientName;

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		if (host == null || "".equals(host)) {
			host = Protocol.DEFAULT_HOST;
		}
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		if ("".equals(password)) {
			password = null;
		}
		this.password = password;
	}

	public int getDatabase() {
		return database;
	}

	public void setDatabase(int database) {
		this.database = database;
	}

	public String getClientName() {
		return clientName;
	}

	public void setClientName(String clientName) {
		if ("".equals(clientName)) {
			clientName = null;
		}
		this.clientName = clientName;
	}

	public int getConnectionTimeout() {
		return connectionTimeout;
	}

	public void setConnectionTimeout(int connectionTimeout) {
		this.connectionTimeout = connectionTimeout;
	}

	public int getSoTimeout() {
		return soTimeout;
	}

	public void setSoTimeout(int soTimeout) {
		this.soTimeout = soTimeout;
	}

	public String getKeyPrefix() {
		return keyPrefix;
	}

	public void setKeyPrefix(String keyPrefix) {
		this.keyPrefix = keyPrefix;
	}


}
