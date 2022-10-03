/*
 * Copyright 2022 cecloud.com All right reserved. This software is the
 * confidential and proprietary information of cecloud.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with cecloud.com .
 */
package com.github.acticfox.mybatis.plugin.cache.redis;

import org.apache.ibatis.cache.decorators.LoggingCache;

/** 
 * @Description: TODO
 * @author kfy May 21, 2022 6:18:11 PM
 * @version V1.0  
 */
public class LoggingRedisCache extends LoggingCache {

	public LoggingRedisCache(String id) {
		super(new MybatisRedisCache(id));
	}

}
