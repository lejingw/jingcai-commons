package com.jingcai.apps.common.jdbc.cache.redis;

import java.util.Set;

/**
 * Created by lejing on 16/4/14.
 */
public interface JedisClient {

	String get(String key);

	Object getObject(String key);

	String set(String key, String value, int timeInSeconds);

	String set(String key, Object object, int timeInSeconds);

	void hset(String key, String field, Object object);

	Object hget(String key, String field);

	Set<String> hkeys(String key);

	void hdel(String key, String field);

	long hlen(String key);

	boolean exists(String key);

	void delete(String key);

	void delete(String... keys);
}
