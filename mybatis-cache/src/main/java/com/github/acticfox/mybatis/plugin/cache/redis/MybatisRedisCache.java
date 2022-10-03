/*
 * Copyright 2022 cecloud.com All right reserved. This software is the
 * confidential and proprietary information of cecloud.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with cecloud.com .
 */
package com.github.acticfox.mybatis.plugin.cache.redis;

import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;

import org.apache.ibatis.cache.Cache;

import com.github.acticfox.mybatis.plugin.common.DummyReadWriteLock;
import com.github.acticfox.mybatis.plugin.common.SerializeUtil;
import com.github.acticfox.mybatis.plugin.common.StringUtils;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/** 
 * @Description: TODO
 * @author kfy May 21, 2022 6:01:33 PM
 * @version V1.0  
 */
public final class MybatisRedisCache implements Cache {

	private final ReadWriteLock readWriteLock = new DummyReadWriteLock();

	private String id;

	private static JedisPool pool;

	private RedisConfig redisConfig;

	public MybatisRedisCache(final String id) {
		if (id == null) {
			throw new IllegalArgumentException("Cache instances require an ID");
		}
		this.id = id;
		redisConfig = RedisConfigurationBuilder.getInstance().parseConfiguration();
		pool = new JedisPool(redisConfig, redisConfig.getHost(), redisConfig.getPort(),
				redisConfig.getConnectionTimeout(), redisConfig.getPassword());
	}

	private Object execute(RedisCallback callback) {
		Jedis jedis = pool.getResource();
		try {
			return callback.doWithRedis(jedis);
		} finally {
			jedis.close();
		}
	}

	@Override
	public String getId() {
		return this.id;
	}

	private String toKeyString(final Object key) {
		String keyString = redisConfig.getKeyPrefix() + StringUtils.sha1Hex(key.toString());
		return keyString;
	}

	@Override
	public int getSize() {
		return (Integer) execute(new RedisCallback() {
			@Override
			public Object doWithRedis(Jedis jedis) {
				Map<byte[], byte[]> result = jedis.hgetAll(id.toString().getBytes());
				return result.size();
			}
		});
	}

	@Override
	public void putObject(final Object key, final Object value) {
		execute(new RedisCallback() {
			@Override
			public Object doWithRedis(Jedis jedis) {
				jedis.hset(id.toString().getBytes(), toKeyString(key).getBytes(), SerializeUtil.serialize(value));

				return null;
			}
		});
	}

	@Override
	public Object getObject(final Object key) {
		return execute(new RedisCallback() {
			@Override
			public Object doWithRedis(Jedis jedis) {
				Object mval = SerializeUtil
						.unserialize(jedis.hget(id.toString().getBytes(), toKeyString(key).getBytes()));
				return mval;
			}
		});
	}

	@Override
	public Object removeObject(final Object key) {
		return execute(new RedisCallback() {
			@Override
			public Object doWithRedis(Jedis jedis) {
				return jedis.hdel(id.toString(), toKeyString(key));
			}
		});
	}

	@Override
	public void clear() {
		execute(new RedisCallback() {
			@Override
			public Object doWithRedis(Jedis jedis) {
				jedis.del(id.toString());
				return null;
			}
		});

	}

	@Override
	public ReadWriteLock getReadWriteLock() {
		return readWriteLock;
	}

	@Override
	public String toString() {
		return "Redis {" + id + "}";
	}

}
