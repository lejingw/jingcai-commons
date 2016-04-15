package com.jingcai.apps.common.jdbc.cache.redis;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.jingcai.apps.common.lang.serialize.ObjectUtils;
import com.jingcai.apps.common.lang.string.StringUtils;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.exceptions.JedisException;

import java.nio.charset.Charset;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Jedis Cache 工具类
 */
@Slf4j
public class JedisUtils implements JedisClient {
	public static final String FORMAT = "%s-%s";
	private JedisPool jedisPool;
	public String keyPrefix;

	public JedisUtils(JedisPool jedisPool, String keyPrefix) {
		this.jedisPool = jedisPool;
		this.keyPrefix = keyPrefix;
	}

	public String getKey(String key) {
		if (null == keyPrefix) {
			return key;
		}
		return String.format(FORMAT, keyPrefix, key);
	}

	/**
	 * 获取缓存
	 *
	 * @param key 键
	 * @return 值
	 */
	public String get(String key) {
		String value = null;
		Jedis jedis = null;
		try {
			jedis = getResource();
			key = getKey(key);
			if (jedis.exists(key)) {
				value = jedis.get(key);
				value = StringUtils.isNotBlank(value) && !"nil".equalsIgnoreCase(value) ? value : null;
				log.debug("get {} = {}", key, value);
			}
		} catch (Exception e) {
			log.warn("get {} = {}", key, value, e);
		} finally {
			returnResource(jedis);
		}
		return value;
	}

	/**
	 * 获取缓存
	 *
	 * @param key 键
	 * @return 值
	 */
	public Object getObject(String key) {
		Object value = null;
		Jedis jedis = null;
		try {
			jedis = getResource();
			key = getKey(key);
			if (jedis.exists(getBytesKey(key))) {
				value = toObject(jedis.get(getBytesKey(key)));
				log.debug("getObject {} = {}", key, value);
			}
		} catch (Exception e) {
			log.warn("getObject {} = {}", key, value, e);
		} finally {
			returnResource(jedis);
		}
		return value;
	}

	/**
	 * 设置缓存
	 *
	 * @param key          键
	 * @param value        值
	 * @param cacheSeconds 超时时间，0为不超时
	 * @return
	 */
	public String set(String key, String value, int cacheSeconds) {
		String result = null;
		Jedis jedis = null;
		try {
			jedis = getResource();
			key = getKey(key);
			result = jedis.set(key, value);
			if (cacheSeconds > 0) {
				jedis.expire(key, cacheSeconds);
			}
			log.debug("set {} = {}", key, value);
		} catch (Exception e) {
			log.warn("set {} = {}", key, value, e);
		} finally {
			returnResource(jedis);
		}
		return result;
	}

	/**
	 * 设置缓存
	 *
	 * @param key          键
	 * @param value        值
	 * @param cacheSeconds 超时时间，0为不超时
	 * @return
	 */
	public String set(String key, Object value, int cacheSeconds) {
		String result = null;
		Jedis jedis = null;
		try {
			jedis = getResource();
			key = getKey(key);
			result = jedis.set(getBytesKey(key), toBytes(value));
			if (cacheSeconds != 0) {
				jedis.expire(key, cacheSeconds);
			}
			log.debug("setObject {} = {}", key, value);
		} catch (Exception e) {
			log.warn("setObject {} = {}", key, value, e);
		} finally {
			returnResource(jedis);
		}
		return result;
	}

	public void hset(String key, String field, Object value) {
		Jedis jedis = null;
		try {
			jedis = getResource();
			key = getKey(key);
			jedis.hset(getBytesKey(key), toBytes(field), toBytes(value));
			log.debug("hset key:{} field:{} = {}", key, field, value);
		} catch (Exception e) {
			log.warn("hset key:{} field:{} = {}", key, field, e);
		} finally {
			returnResource(jedis);
		}
	}

	public Object hget(String key, String field) {
		Object value = null;
		Jedis jedis = null;
		try {
			jedis = getResource();
			key = getKey(key);
			byte[] bytes = jedis.hget(getBytesKey(key), toBytes(field));
			value = toObject(bytes);
			log.debug("hget key:{} field:{} = {}", key, field, value);
		} catch (Exception e) {
			log.warn("hget key:{} field:{} = {}", key, field, e);
		} finally {
			returnResource(jedis);
		}
		return value;
	}

	public Set<String> hkeys(String key) {
		Set<String> keys = new HashSet<String>();
		Jedis jedis = null;
		try {
			jedis = getResource();
			keys = jedis.hkeys(getKey(key));
			log.debug("hkeys key:{} = {}", key, keys);
		} catch (Exception e) {
			log.warn("hkeys key:{} {} = {}", key, e);
		} finally {
			returnResource(jedis);
		}
		return keys;
	}

	public void hdel(String key, String field) {
		Jedis jedis = null;
		try {
			jedis = getResource();
			key = getKey(key);
			jedis.hdel(key, field);
			log.debug("hdel key:{} field:{}", key, field);
		} finally {
			returnResource(jedis);
		}
	}

	public long hlen(String key) {
		Jedis jedis = null;
		try {
			jedis = getResource();
			key = getKey(key);
			Long hlen = jedis.hlen(key);
			log.debug("hlen key:{} = {}", key, hlen);
			return hlen;
		} finally {
			returnResource(jedis);
		}
	}

	public boolean exists(String key) {
		boolean result = false;
		Jedis jedis = null;
		try {
			jedis = getResource();
			key = getKey(key);
			result = jedis.exists(key);
			log.debug("exists {} {}", key, result);
		} catch (Exception e) {
			log.warn("exists {}", key, e);
		} finally {
			returnResource(jedis);
		}
		return result;
	}

	public void delete(String key) {
		Jedis jedis = null;
		try {
			jedis = getResource();
			key = getKey(key);
			log.debug("del key:{}", key);
			jedis.del(key);
		} finally {
			returnResource(jedis);
		}
	}

	public void delete(String... keys) {
		List<String> list = Lists.newArrayListWithCapacity(keys.length);
		for (String key : keys) {
			list.add(getKey(key));
		}
		Jedis jedis = null;
		try {
			jedis = getResource();
			jedis.del(list.toArray(new String[0]));
		} finally {
			returnResource(jedis);
		}
	}


	/**
	 * 获取List缓存
	 *
	 * @param key 键
	 * @return 值
	 */
	public List<String> getList(String key) {
		List<String> value = null;
		Jedis jedis = null;
		try {
			jedis = getResource();
			if (jedis.exists(key)) {
				value = jedis.lrange(key, 0, -1);
				log.debug("getList {} = {}", key, value);
			}
		} catch (Exception e) {
			log.warn("getList {} = {}", key, value, e);
		} finally {
			returnResource(jedis);
		}
		return value;
	}

	/**
	 * 获取List缓存
	 *
	 * @param key 键
	 * @return 值
	 */
	public List<Object> getObjectList(String key) {
		List<Object> value = null;
		Jedis jedis = null;
		try {
			jedis = getResource();
			if (jedis.exists(getBytesKey(key))) {
				List<byte[]> list = jedis.lrange(getBytesKey(key), 0, -1);
				value = Lists.newArrayList();
				for (byte[] bs : list) {
					value.add(toObject(bs));
				}
				log.debug("getObjectList {} = {}", key, value);
			}
		} catch (Exception e) {
			log.warn("getObjectList {} = {}", key, value, e);
		} finally {
			returnResource(jedis);
		}
		return value;
	}

	/**
	 * 设置List缓存
	 *
	 * @param key          键
	 * @param value        值
	 * @param cacheSeconds 超时时间，0为不超时
	 * @return
	 */
	public long setList(String key, List<String> value, int cacheSeconds) {
		long result = 0;
		Jedis jedis = null;
		try {
			jedis = getResource();
			if (jedis.exists(key)) {
				jedis.del(key);
			}
			result = jedis.rpush(key, (String[]) value.toArray());
			if (cacheSeconds != 0) {
				jedis.expire(key, cacheSeconds);
			}
			log.debug("setList {} = {}", key, value);
		} catch (Exception e) {
			log.warn("setList {} = {}", key, value, e);
		} finally {
			returnResource(jedis);
		}
		return result;
	}

	/**
	 * 设置List缓存
	 *
	 * @param key          键
	 * @param value        值
	 * @param cacheSeconds 超时时间，0为不超时
	 * @return
	 */
	public long setObjectList(String key, List value, int cacheSeconds) {
		long result = 0;
		Jedis jedis = null;
		try {
			jedis = getResource();
			if (jedis.exists(getBytesKey(key))) {
				jedis.del(key);
			}
//			List<byte[]> list = Lists.newArrayList();
//			for (Object o : value){
//				list.add(toBytes(o));
//			}
//			result = jedis.rpush(getBytesKey(key), (byte[][])list.toArray());

			byte[][] arr = new byte[value.size()][];
			for (int i = 0; i < value.size(); i++) {
				arr[i] = toBytes(value.get(i));
			}
			result = jedis.rpush(getBytesKey(key), arr);
			if (cacheSeconds != 0) {
				jedis.expire(key, cacheSeconds);
			}
			log.debug("setObjectList {} = {}", key, value);
		} catch (Exception e) {
			log.warn("setObjectList {} = {}", key, value, e);
		} finally {
			returnResource(jedis);
		}
		return result;
	}

	/**
	 * 向List缓存中添加值
	 *
	 * @param key   键
	 * @param value 值
	 * @return
	 */
	public long listAdd(String key, String... value) {
		long result = 0;
		Jedis jedis = null;
		try {
			jedis = getResource();
			result = jedis.rpush(key, value);
			log.debug("listAdd {} = {}", key, value);
		} catch (Exception e) {
			log.warn("listAdd {} = {}", key, value, e);
		} finally {
			returnResource(jedis);
		}
		return result;
	}

	/**
	 * 向List缓存中添加值
	 *
	 * @param key   键
	 * @param value 值
	 * @return
	 */
	public long listObjectAdd(String key, Object... value) {
		long result = 0;
		Jedis jedis = null;
		try {
			jedis = getResource();
			List<byte[]> list = Lists.newArrayList();
			for (Object o : value) {
				list.add(toBytes(o));
			}
			result = jedis.rpush(getBytesKey(key), (byte[][]) list.toArray());
			log.debug("listObjectAdd {} = {}", key, value);
		} catch (Exception e) {
			log.warn("listObjectAdd {} = {}", key, value, e);
		} finally {
			returnResource(jedis);
		}
		return result;
	}

	/**
	 * 获取缓存
	 *
	 * @param key 键
	 * @return 值
	 */
	public Set<String> getSet(String key) {
		Set<String> value = null;
		Jedis jedis = null;
		try {
			jedis = getResource();
			if (jedis.exists(key)) {
				value = jedis.smembers(key);
				log.debug("getSet {} = {}", key, value);
			}
		} catch (Exception e) {
			log.warn("getSet {} = {}", key, value, e);
		} finally {
			returnResource(jedis);
		}
		return value;
	}

	/**
	 * 获取缓存
	 *
	 * @param key 键
	 * @return 值
	 */
	public Set<Object> getObjectSet(String key) {
		Set<Object> value = null;
		Jedis jedis = null;
		try {
			jedis = getResource();
			if (jedis.exists(getBytesKey(key))) {
				value = Sets.newHashSet();
				Set<byte[]> set = jedis.smembers(getBytesKey(key));
				for (byte[] bs : set) {
					value.add(toObject(bs));
				}
				log.debug("getObjectSet {} = {}", key, value);
			}
		} catch (Exception e) {
			log.warn("getObjectSet {} = {}", key, value, e);
		} finally {
			returnResource(jedis);
		}
		return value;
	}

	/**
	 * 设置Set缓存
	 *
	 * @param key          键
	 * @param value        值
	 * @param cacheSeconds 超时时间，0为不超时
	 * @return
	 */
	public long setSet(String key, Set<String> value, int cacheSeconds) {
		long result = 0;
		Jedis jedis = null;
		try {
			jedis = getResource();
			if (jedis.exists(key)) {
				jedis.del(key);
			}
			result = jedis.sadd(key, (String[]) value.toArray());
			if (cacheSeconds != 0) {
				jedis.expire(key, cacheSeconds);
			}
			log.debug("setSet {} = {}", key, value);
		} catch (Exception e) {
			log.warn("setSet {} = {}", key, value, e);
		} finally {
			returnResource(jedis);
		}
		return result;
	}

	/**
	 * 设置Set缓存
	 *
	 * @param key          键
	 * @param value        值
	 * @param cacheSeconds 超时时间，0为不超时
	 * @return
	 */
	public long setObjectSet(String key, Set<Object> value, int cacheSeconds) {
		long result = 0;
		Jedis jedis = null;
		try {
			jedis = getResource();
			if (jedis.exists(getBytesKey(key))) {
				jedis.del(key);
			}
			Set<byte[]> set = Sets.newHashSet();
			for (Object o : value) {
				set.add(toBytes(o));
			}
			result = jedis.sadd(getBytesKey(key), (byte[][]) set.toArray());
			if (cacheSeconds != 0) {
				jedis.expire(key, cacheSeconds);
			}
			log.debug("setObjectSet {} = {}", key, value);
		} catch (Exception e) {
			log.warn("setObjectSet {} = {}", key, value, e);
		} finally {
			returnResource(jedis);
		}
		return result;
	}

	/**
	 * 向Set缓存中添加值
	 *
	 * @param key   键
	 * @param value 值
	 * @return
	 */
	public long setSetAdd(String key, String... value) {
		long result = 0;
		Jedis jedis = null;
		try {
			jedis = getResource();
			result = jedis.sadd(key, value);
			log.debug("setSetAdd {} = {}", key, value);
		} catch (Exception e) {
			log.warn("setSetAdd {} = {}", key, value, e);
		} finally {
			returnResource(jedis);
		}
		return result;
	}

	/**
	 * 向Set缓存中添加值
	 *
	 * @param key   键
	 * @param value 值
	 * @return
	 */
	public long setSetObjectAdd(String key, Object... value) {
		long result = 0;
		Jedis jedis = null;
		try {
			jedis = getResource();
			Set<byte[]> set = Sets.newHashSet();
			for (Object o : value) {
				set.add(toBytes(o));
			}
			result = jedis.rpush(getBytesKey(key), (byte[][]) set.toArray());
			log.debug("setSetObjectAdd {} = {}", key, value);
		} catch (Exception e) {
			log.warn("setSetObjectAdd {} = {}", key, value, e);
		} finally {
			returnResource(jedis);
		}
		return result;
	}

	/**
	 * 获取Map缓存
	 *
	 * @param key 键
	 * @return 值
	 */
	public Map<String, String> getMap(String key) {
		Map<String, String> value = null;
		Jedis jedis = null;
		try {
			jedis = getResource();
			if (jedis.exists(key)) {
				value = jedis.hgetAll(key);
				log.debug("getMap {} = {}", key, value);
			}
		} catch (Exception e) {
			log.warn("getMap {} = {}", key, value, e);
		} finally {
			returnResource(jedis);
		}
		return value;
	}

	/**
	 * 获取Map缓存
	 *
	 * @param key 键
	 * @return 值
	 */
	public Map<String, Object> getObjectMap(String key) {
		Map<String, Object> value = null;
		Jedis jedis = null;
		try {
			jedis = getResource();
			if (jedis.exists(getBytesKey(key))) {
				value = Maps.newHashMap();
				Map<byte[], byte[]> map = jedis.hgetAll(getBytesKey(key));
				for (Map.Entry<byte[], byte[]> e : map.entrySet()) {
					value.put(StringUtils.toEncodedString(e.getKey(), Charset.defaultCharset()), toObject(e.getValue()));
				}
				log.debug("getObjectMap {} = {}", key, value);
			}
		} catch (Exception e) {
			log.warn("getObjectMap {} = {}", key, value, e);
		} finally {
			returnResource(jedis);
		}
		return value;
	}

	/**
	 * 设置Map缓存
	 *
	 * @param key          键
	 * @param value        值
	 * @param cacheSeconds 超时时间，0为不超时
	 * @return
	 */
	public String setMap(String key, Map<String, String> value, int cacheSeconds) {
		String result = null;
		Jedis jedis = null;
		try {
			jedis = getResource();
			if (jedis.exists(key)) {
				jedis.del(key);
			}
			result = jedis.hmset(key, value);
			if (cacheSeconds != 0) {
				jedis.expire(key, cacheSeconds);
			}
			log.debug("setMap {} = {}", key, value);
		} catch (Exception e) {
			log.warn("setMap {} = {}", key, value, e);
		} finally {
			returnResource(jedis);
		}
		return result;
	}

	/**
	 * 设置Map缓存
	 *
	 * @param key          键
	 * @param value        值
	 * @param cacheSeconds 超时时间，0为不超时
	 * @return
	 */
	public String setObjectMap(String key, Map<String, Object> value, int cacheSeconds) {
		String result = null;
		Jedis jedis = null;
		try {
			jedis = getResource();
			if (jedis.exists(getBytesKey(key))) {
				jedis.del(key);
			}
			Map<byte[], byte[]> map = Maps.newHashMap();
			for (Map.Entry<String, Object> e : value.entrySet()) {
				map.put(getBytesKey(e.getKey()), toBytes(e.getValue()));
			}
			result = jedis.hmset(getBytesKey(key), (Map<byte[], byte[]>) map);
			if (cacheSeconds != 0) {
				jedis.expire(key, cacheSeconds);
			}
			log.debug("setObjectMap {} = {}", key, value);
		} catch (Exception e) {
			log.warn("setObjectMap {} = {}", key, value, e);
		} finally {
			returnResource(jedis);
		}
		return result;
	}

	/**
	 * 向Map缓存中添加值
	 *
	 * @param key   键
	 * @param value 值
	 * @return
	 */
	public String mapPut(String key, Map<String, String> value) {
		String result = null;
		Jedis jedis = null;
		try {
			jedis = getResource();
			result = jedis.hmset(key, value);
			log.debug("mapPut {} = {}", key, value);
		} catch (Exception e) {
			log.warn("mapPut {} = {}", key, value, e);
		} finally {
			returnResource(jedis);
		}
		return result;
	}

	/**
	 * 向Map缓存中添加值
	 *
	 * @param key   键
	 * @param value 值
	 * @return
	 */
	public String mapObjectPut(String key, Map<String, Object> value) {
		String result = null;
		Jedis jedis = null;
		try {
			jedis = getResource();
			Map<byte[], byte[]> map = Maps.newHashMap();
			for (Map.Entry<String, Object> e : value.entrySet()) {
				map.put(getBytesKey(e.getKey()), toBytes(e.getValue()));
			}
			result = jedis.hmset(getBytesKey(key), (Map<byte[], byte[]>) map);
			log.debug("mapObjectPut {} = {}", key, value);
		} catch (Exception e) {
			log.warn("mapObjectPut {} = {}", key, value, e);
		} finally {
			returnResource(jedis);
		}
		return result;
	}

	/**
	 * 移除Map缓存中的值
	 *
	 * @param key    键
	 * @param mapKey 值
	 * @return
	 */
	public long mapRemove(String key, String mapKey) {
		long result = 0;
		Jedis jedis = null;
		try {
			jedis = getResource();
			result = jedis.hdel(key, mapKey);
			log.debug("mapRemove {}  {}", key, mapKey);
		} catch (Exception e) {
			log.warn("mapRemove {}  {}", key, mapKey, e);
		} finally {
			returnResource(jedis);
		}
		return result;
	}

	/**
	 * 移除Map缓存中的值
	 *
	 * @param key    键
	 * @param mapKey 值
	 * @return
	 */
	public long mapObjectRemove(String key, String mapKey) {
		long result = 0;
		Jedis jedis = null;
		try {
			jedis = getResource();
			result = jedis.hdel(getBytesKey(key), getBytesKey(mapKey));
			log.debug("mapObjectRemove {}  {}", key, mapKey);
		} catch (Exception e) {
			log.warn("mapObjectRemove {}  {}", key, mapKey, e);
		} finally {
			returnResource(jedis);
		}
		return result;
	}

	/**
	 * 判断Map缓存中的Key是否存在
	 *
	 * @param key    键
	 * @param mapKey 值
	 * @return
	 */
	public boolean mapExists(String key, String mapKey) {
		boolean result = false;
		Jedis jedis = null;
		try {
			jedis = getResource();
			result = jedis.hexists(key, mapKey);
			log.debug("mapExists {}  {}", key, mapKey);
		} catch (Exception e) {
			log.warn("mapExists {}  {}", key, mapKey, e);
		} finally {
			returnResource(jedis);
		}
		return result;
	}

	/**
	 * 判断Map缓存中的Key是否存在
	 *
	 * @param key    键
	 * @param mapKey 值
	 * @return
	 */
	public boolean mapObjectExists(String key, String mapKey) {
		boolean result = false;
		Jedis jedis = null;
		try {
			jedis = getResource();
			result = jedis.hexists(getBytesKey(key), getBytesKey(mapKey));
			log.debug("mapObjectExists {}  {}", key, mapKey);
		} catch (Exception e) {
			log.warn("mapObjectExists {}  {}", key, mapKey, e);
		} finally {
			returnResource(jedis);
		}
		return result;
	}

	/**
	 * 删除缓存
	 *
	 * @param key 键
	 * @return
	 */
	public long del(String key) {
		long result = 0;
		Jedis jedis = null;
		try {
			jedis = getResource();
			if (jedis.exists(key)) {
				result = jedis.del(key);
				log.debug("del {}", key);
			} else {
				log.debug("del {} not exists", key);
			}
		} catch (Exception e) {
			log.warn("del {}", key, e);
		} finally {
			returnResource(jedis);
		}
		return result;
	}

	/**
	 * 删除缓存
	 *
	 * @param key 键
	 * @return
	 */
	public long delObject(String key) {
		long result = 0;
		Jedis jedis = null;
		try {
			jedis = getResource();
			if (jedis.exists(getBytesKey(key))) {
				result = jedis.del(getBytesKey(key));
				log.debug("delObject {}", key);
			} else {
				log.debug("delObject {} not exists", key);
			}
		} catch (Exception e) {
			log.warn("delObject {}", key, e);
		} finally {
			returnResource(jedis);
		}
		return result;
	}

	/**
	 * 缓存是否存在
	 *
	 * @param key 键
	 * @return
	 */
	public boolean existsObject(String key) {
		boolean result = false;
		Jedis jedis = null;
		try {
			jedis = getResource();
			result = jedis.exists(getBytesKey(key));
			log.debug("existsObject {}", key);
		} catch (Exception e) {
			log.warn("existsObject {}", key, e);
		} finally {
			returnResource(jedis);
		}
		return result;
	}

	/**
	 * 获取资源
	 *
	 * @return
	 * @throws JedisException
	 */
	public Jedis getResource() throws JedisException {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
//			log.debug("getResource.", jedis);
		} catch (JedisException e) {
			log.warn("getResource.", e);
			returnResource(jedis);
			throw e;
		}
		return jedis;
	}

	/**
	 * 释放资源
	 *
	 * @param jedis
	 */
	public void returnResource(Jedis jedis) {
		if (jedis != null) {
			jedis.close();
		}
	}

	/**
	 * 获取byte[]类型Key
	 *
	 * @param object
	 * @return
	 */
	public byte[] getBytesKey(Object object) {
		if (object instanceof String) {
			return ((String) object).getBytes(Charset.defaultCharset());
		} else {
			return ObjectUtils.serialize(object);
		}
	}

	/**
	 * Object转换byte[]类型
	 *
	 * @param object
	 * @return
	 */
	public byte[] toBytes(Object object) {
		return ObjectUtils.serialize(object);
	}

	/**
	 * byte[]型转换Object
	 *
	 * @param bytes
	 * @return
	 */
	public Object toObject(byte[] bytes) {
		return ObjectUtils.unserialize(bytes);
	}

}
