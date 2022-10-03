/*
 * Copyright 2022 cecloud.com All right reserved. This software is the
 * confidential and proprietary information of cecloud.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with cecloud.com .
 */
package com.github.acticfox.mybatis.plugin.cache.redis;

import redis.clients.jedis.Jedis;

/** 
 * @Description: TODO
 * @author kfy May 21, 2022 6:05:01 PM
 * @version V1.0  
 */
public interface RedisCallback {

	Object doWithRedis(Jedis jedis);

}
