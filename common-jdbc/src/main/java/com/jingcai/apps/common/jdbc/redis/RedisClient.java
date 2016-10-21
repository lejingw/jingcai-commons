package com.jingcai.apps.common.jdbc.redis;

import com.jingcai.apps.common.jdbc.redis.config.RedisHAClientConfig;
import com.jingcai.apps.common.lang.format.JsonMapper;
import com.jingcai.apps.common.lang.string.StringUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Protocol;
import redis.clients.util.SafeEncoder;

import java.io.*;
import java.util.*;
import java.util.Map.Entry;

/**
 * 封装Jedis API，提供redis命令调用的操作
 */
public class RedisClient implements RedisOperation {

	protected final Logger logger = LoggerFactory.getLogger(this.getClass());

	private String cacheName = "default";

	private String redisServerHost = "localhost";

	private int redisServerPort = Protocol.DEFAULT_PORT;

	private String redisAuthKey;

	private JedisPool jedisPool;

	private boolean isAlive = true;

	private int timeout = Protocol.DEFAULT_TIMEOUT;

	private int maxIdle = GenericObjectPoolConfig.DEFAULT_MAX_IDLE;
	private long maxWaitMillis = GenericObjectPoolConfig.DEFAULT_MAX_WAIT_MILLIS;//DEFAULT_MAX_WAIT;
	private boolean testOnBorrow = GenericObjectPoolConfig.DEFAULT_TEST_ON_BORROW;
	private int minIdle = GenericObjectPoolConfig.DEFAULT_MIN_IDLE;
	private boolean testOnReturn = GenericObjectPoolConfig.DEFAULT_TEST_ON_RETURN;
	private boolean testWhileIdle = GenericObjectPoolConfig.DEFAULT_TEST_WHILE_IDLE;
	private long timeBetweenEvictionRunsMillis = GenericObjectPoolConfig.DEFAULT_TIME_BETWEEN_EVICTION_RUNS_MILLIS;
	private int numTestsPerEvictionRun = GenericObjectPoolConfig.DEFAULT_NUM_TESTS_PER_EVICTION_RUN;
	private long minEvictableIdleTimeMillis = GenericObjectPoolConfig.DEFAULT_MIN_EVICTABLE_IDLE_TIME_MILLIS;
	private long softMinEvictableIdleTimeMillis = GenericObjectPoolConfig.DEFAULT_SOFT_MIN_EVICTABLE_IDLE_TIME_MILLIS;
	private boolean lifo = GenericObjectPoolConfig.DEFAULT_LIFO;
	private boolean blockWhenExhausted = GenericObjectPoolConfig.DEFAULT_BLOCK_WHEN_EXHAUSTED;

	/**
	 * Creates a new instance of RedisClient.
	 */
	public RedisClient(RedisHAClientConfig clientConfig) {
		if (clientConfig == null) {
			throw new IllegalArgumentException("Client config is null");
		}
		this.cacheName = clientConfig.getCacheName();
		this.redisServerHost = clientConfig.getRedisServerHost();
		this.redisServerPort = clientConfig.getRedisServerPort();
		this.timeout = clientConfig.getTimeout();
		this.redisAuthKey = clientConfig.getRedisAuthKey();
		if (StringUtils.isEmpty(redisAuthKey)) {
			logger.debug("use no auth mode for " + redisServerHost);
			jedisPool = new JedisPool(getPoolConfig(), redisServerHost, redisServerPort, timeout);
		} else {
			jedisPool = new JedisPool(getPoolConfig(), redisServerHost, redisServerPort, timeout, redisAuthKey);
		}
		onAfterInit(redisServerHost, redisServerPort);
	}

	protected void onAfterInit(String host, int port) {
		logger.debug("New Jedis pool <client: " + cacheName + "> <server: " + this.getLiteralRedisServer() +
				"> object created. Connection pool will be initiated when calling.");
	}

	private GenericObjectPoolConfig getPoolConfig() {
		JedisPoolConfig poolConfig = new JedisPoolConfig();
		// maxIdle为负数时，不对pool size大小做限制，此处做限制，防止保持过多空闲redis连接
		if (this.maxIdle >= 0) {
			poolConfig.setMaxIdle(this.maxIdle);
		}
		poolConfig.setMaxWaitMillis(this.maxWaitMillis);
		poolConfig.setBlockWhenExhausted(this.blockWhenExhausted);
		poolConfig.setTestOnBorrow(this.testOnBorrow);
		poolConfig.setMinIdle(this.minIdle);
		poolConfig.setTestOnReturn(this.testOnReturn);
		poolConfig.setTestWhileIdle(this.testWhileIdle);
		poolConfig.setTimeBetweenEvictionRunsMillis(this.timeBetweenEvictionRunsMillis);
		poolConfig.setNumTestsPerEvictionRun(this.numTestsPerEvictionRun);
		poolConfig.setMinEvictableIdleTimeMillis(this.minEvictableIdleTimeMillis);
		poolConfig.setSoftMinEvictableIdleTimeMillis(this.softMinEvictableIdleTimeMillis);
		poolConfig.setLifo(this.lifo);
		return poolConfig;
	}

	public String ping() {
		Jedis jedis = null;
		try {
			jedis = this.jedisPool.getResource();
			String pong = jedis.ping();
			return pong;
		} catch (Exception e) {
			logger.debug(e.getMessage());
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
		return StringUtils.EMPTY;
	}

	/**
	 * get old value and set new value
	 */
	public String getSet(String key, String value, int expiration) throws Exception {
		Jedis jedis = null;
		try {
			jedis = this.jedisPool.getResource();
			long begin = System.currentTimeMillis();
			// 操作expire成功返回1，失败返回0，仅当均返回1时，实际操作成功
			String val = jedis.getSet(key, value);

			boolean success = true;
			if (expiration > 0) {
				Long res = jedis.expire(key, expiration);
				if (res == 0L) {
					success = false;
				}
			}
			long end = System.currentTimeMillis();
			if (success) {
				logger.debug("getset key:" + key + ", spends: " + (end - begin) + "ms");
			} else {
				logger.debug("getset key: " + key + " failed, key has already exists! ");
			}

			return val;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw e;
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	/**
	 * get old value and set new value
	 */
	public Object getSetObject(String key, Object value, int expiration) throws Exception {
		Jedis jedis = null;
		try {
			jedis = this.jedisPool.getResource();
			long begin = System.currentTimeMillis();
			// 操作expire成功返回1，失败返回0，仅当均返回1时，实际操作成功
			byte[] val = jedis.getSet(SafeEncoder.encode(key), serialize(value));
			Object result = deserialize(val);

			boolean success = true;
			if (expiration > 0) {
				Long res = jedis.expire(key, expiration);
				if (res == 0L) {
					success = false;
				}
			}
			long end = System.currentTimeMillis();
			if (success) {
				logger.debug("getset key:" + key + ", spends: " + (end - begin) + "ms");
			} else {
				logger.debug("getset key: " + key + " failed, key has already exists! ");
			}

			return result;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw e;
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	/**
	 * get value<br>
	 * return null if key did not exist
	 *
	 * @param key
	 * @return
	 * @throws Exception
	 */
	public String get(String key) throws Exception {
		String data;
		Jedis jedis = null;
		try {
			jedis = this.jedisPool.getResource();
			long begin = System.currentTimeMillis();
			data = jedis.get(key);
			long end = System.currentTimeMillis();
			logger.debug("get key:" + key + ", spends: " + (end - begin) + "ms");
		} catch (Exception e) {
			// do jedis.quit() and jedis.disconnect()
			throw e;
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
		return data;
	}

	/**
	 * get value<br>
	 * return null if key did not exist
	 *
	 * @param key
	 * @return
	 * @throws Exception
	 */
	public Object getObject(String key) throws Exception {
		byte[] data = null;
		Jedis jedis = null;
		try {
			jedis = this.jedisPool.getResource();
			long begin = System.currentTimeMillis();
			data = jedis.get(SafeEncoder.encode(key));
			long end = System.currentTimeMillis();
			logger.debug("get key:" + key + ", spends: " + (end - begin) + "ms");
		} catch (Exception e) {
			// do jedis.quit() and jedis.disconnect()
			throw e;
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
		return this.deserialize(data);
	}

	/**
	 * value set
	 */
	public boolean set(String key, String value, int expiration) throws Exception {
		String result = "";
		Jedis jedis = null;
		try {
			jedis = this.jedisPool.getResource();

			long begin = System.currentTimeMillis();
			if (expiration > 0) {
				result = jedis.setex(key, expiration, value);
			} else {
				result = jedis.set(key, value);
			}
			long end = System.currentTimeMillis();
			logger.debug("set key:" + key + ", spends: " + (end - begin) + "ms");
			return "OK".equalsIgnoreCase(result);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw e;
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	/**
	 * value set
	 */
	public boolean setObject(String key, Object value, int expiration) throws Exception {
		String result = "";
		Jedis jedis = null;
		try {
			jedis = this.jedisPool.getResource();

			long begin = System.currentTimeMillis();
			if (expiration > 0) {
				result = jedis.setex(SafeEncoder.encode(key), expiration, serialize(value));
			} else {
				result = jedis.set(SafeEncoder.encode(key), serialize(value));
			}
			long end = System.currentTimeMillis();
			logger.debug("set key:" + key + ", spends: " + (end - begin) + "ms");
			return "OK".equalsIgnoreCase(result);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw e;
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}

	}

	public boolean set(String key, String value) throws Exception {
		return this.set(key, value, -1);
	}

	public boolean setObject(String key, Object value) throws Exception {
		return this.setObject(key, value, -1);
	}

	/**
	 * add if not exists
	 */
	public boolean add(String key, String value, int expiration) throws Exception {
		Jedis jedis = null;
		try {
			jedis = this.jedisPool.getResource();
			long begin = System.currentTimeMillis();
			// 操作setnx与expire成功返回1，失败返回0，仅当均返回1时，实际操作成功
			Long result = jedis.setnx(key, value);
			if (expiration > 0) {
				result = result & jedis.expire(key, expiration);
			}
			long end = System.currentTimeMillis();
			if (result == 1L) {
				logger.debug("add key:" + key + ", spends: " + (end - begin) + "ms");
			} else {
				logger.debug("add key: " + key + " failed, key has already exists! ");
			}
			return result == 1L;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw e;
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	public boolean addObject(String key, Object value, int expiration) throws Exception {
		Jedis jedis = null;
		try {
			jedis = this.jedisPool.getResource();
			long begin = System.currentTimeMillis();
			// 操作setnx与expire成功返回1，失败返回0，仅当均返回1时，实际操作成功
			byte[] encodeKey = SafeEncoder.encode(key);
			Long result = jedis.setnx(encodeKey, serialize(value));
			if (expiration > 0) {
				result = result & jedis.expire(encodeKey, expiration);
			}
			long end = System.currentTimeMillis();
			if (result == 1L) {
				logger.debug("add key:" + key + ", spends: " + (end - begin) + "ms");
			} else {
				logger.debug("add key: " + key + " failed, key has already exists! ");
			}
			return result == 1L;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw e;
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	public boolean add(String key, String value) throws Exception {
		return this.add(key, value, -1);
	}

	public boolean addObject(String key, Object value) throws Exception {
		return this.addObject(key, value, -1);
	}

	public boolean exists(String key) throws Exception {
		boolean isExist = false;
		Jedis jedis = null;
		try {
			jedis = this.jedisPool.getResource();
			isExist = jedis.exists(key);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw e;
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
		return isExist;
	}

	public boolean delete(String key) {
		Jedis jedis = null;
		try {
			jedis = this.jedisPool.getResource();
			jedis.del(key);
			logger.debug("delete key:" + key);
			return true;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
		return false;
	}

	public boolean expire(String key, int seconds) {
		Jedis jedis = null;
		try {
			jedis = this.jedisPool.getResource();
			jedis.expire(key, seconds);
			logger.debug("expire key:" + key + " time after " + seconds + " seconds.");
			return true;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
		return false;
	}

	/**
	 * Delete all the keys of all the existing databases, not just the currently selected one.
	 *
	 * @return false if redis did not execute the option
	 */
	public boolean flushall() {
		String result = "";
		Jedis jedis = null;
		try {
			jedis = this.jedisPool.getResource();
			result = jedis.flushAll();
			logger.debug("redis client name: " + this.getCacheName() + " flushall.");

		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
		return "OK".equalsIgnoreCase(result);
	}

	public void shutdown() {
		try {
			this.jedisPool.destroy();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

	/**
	 * Get the bytes representing the given serialized object.
	 */
	protected byte[] serialize(Object o) {
		if (o == null) {
			// throw new NullPointerException("Can't serialize null");
			return new byte[0];
		}
		byte[] rv = null;
		if (o instanceof String) {
			try {
				rv = ((String) o).getBytes("utf-8");
			} catch (UnsupportedEncodingException e) {
			}
			return rv;
		}
		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ObjectOutputStream os = new ObjectOutputStream(bos);
			os.writeObject(o);
			os.close();
			bos.close();
			rv = bos.toByteArray();
		} catch (IOException e) {
			throw new IllegalArgumentException("Non-serializable object", e);
		}
		return rv;
	}

	/**
	 * Get the object represented by the given serialized bytes.
	 */
	protected Object deserialize(byte[] in) {
		Object rv = null;
		try {
			if (in != null) {
				ByteArrayInputStream bis = new ByteArrayInputStream(in);
				ObjectInputStream is = new ObjectInputStream(bis);
				rv = is.readObject();
				is.close();
				bis.close();
			}
		} catch (IOException e) {
			logger.warn("Caught IOException decoding %d bytes of data", e);
		} catch (ClassNotFoundException e) {
			logger.warn("Caught CNFE decoding %d bytes of data", e);
		}
		return rv;
	}

	public void hput(String key, String field, String fieldValue) throws Exception {
		Jedis jedis = null;
		try {
			jedis = this.jedisPool.getResource();
			jedis.hset(key, field, fieldValue);
			logger.debug("hset key:" + key + " field:" + field);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw e;
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	public void hputObject(String key, String field, Object fieldValue) throws Exception {
		Jedis jedis = null;
		try {
			jedis = this.jedisPool.getResource();
			jedis.hset(SafeEncoder.encode(key), SafeEncoder.encode(field), serialize(fieldValue));
			logger.debug("hset key:" + key + " field:" + field);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw e;
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	public String hget(String key, String field) {
		Jedis jedis = null;
		try {
			jedis = this.jedisPool.getResource();
			String value = jedis.hget(key, field);
			logger.debug("hget key:" + key + " field:" + field);
			return value;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
		return null;
	}

	public Object hgetObject(String key, String field) {
		Jedis jedis = null;
		try {
			jedis = this.jedisPool.getResource();
			byte[] value = jedis.hget(SafeEncoder.encode(key), SafeEncoder.encode(field));
			logger.debug("hget key:" + key + " field:" + field);
			return deserialize(value);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
		return null;
	}

	public boolean hdel(String key, String field) throws Exception {
		Jedis jedis = null;
		try {
			jedis = this.jedisPool.getResource();
			long value = jedis.hdel(key, field);
			logger.debug("hget key:" + key + ", field:" + field);
			return value == 1;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw e;
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	public Set<String> hKeys(String key) throws Exception {
		Jedis jedis = null;
		try {
			jedis = this.jedisPool.getResource();
			Set<String> hkeys = jedis.hkeys(key);
			logger.debug("hkeys key:" + key);
			if (isEmpty(hkeys)) {
				return new HashSet<>(1);
			}
			return hkeys;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw e;
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	public List<String> hValues(String key) throws Exception {
		Jedis jedis = null;
		try {
			jedis = this.jedisPool.getResource();
			List<String> hvals = jedis.hvals(key);
			logger.debug("hvals key:" + key);
			if (isEmpty(hvals)) {
				return new ArrayList<>(1);
			}
			return hvals;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw e;
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	public List<Object> hValuesObject(String key) throws Exception {
		Jedis jedis = null;
		try {
			jedis = this.jedisPool.getResource();
			List<byte[]> hvals = jedis.hvals(SafeEncoder.encode(key));
			logger.debug("hvals key:" + key);
			if (isEmpty(hvals)) {
				return new ArrayList<Object>(1);
			} else {
				List<Object> ret = new ArrayList<Object>(hvals.size());
				for (byte[] bb : hvals) {
					ret.add(deserialize(bb));
				}
				return ret;
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw e;
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	public boolean hExists(String key, String field) throws Exception {
		Jedis jedis = null;
		try {
			jedis = this.jedisPool.getResource();
			boolean ret = jedis.hexists(key, field);
			logger.debug("hexists key:" + key + ", field:" + field);
			return ret;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw e;
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	public long hLen(String key) throws Exception {
		Jedis jedis = null;
		try {
			jedis = this.jedisPool.getResource();
			long ret = jedis.hlen(key);
			logger.debug("hlen key:" + key);
			return ret;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw e;
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	private Map<String, Object> decodeMap(final Map<byte[], byte[]> values) {
		if (isEmpty(values)) {
			return Collections.emptyMap();
		}
		Map<byte[], byte[]> copy = new HashMap<byte[], byte[]>(values);
		Iterator<Entry<byte[], byte[]>> iterator = copy.entrySet().iterator();
		Map<String, Object> ret = new HashMap<String, Object>();
		while (iterator.hasNext()) {
			Entry<byte[], byte[]> next = iterator.next();
			ret.put(SafeEncoder.encode(next.getKey()), deserialize(next.getValue()));
		}

		return ret;
	}

	public Map<String, String> hGetAll(String key) throws Exception {
		Jedis jedis = null;
		try {
			jedis = this.jedisPool.getResource();
			Map<String, String> hgetAll = jedis.hgetAll(key);
			logger.debug("hgetAll key:" + key);
			return hgetAll;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw e;
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	public Map<String, Object> hGetAllObject(String key) throws Exception {
		Jedis jedis = null;
		try {
			jedis = this.jedisPool.getResource();
			Map<byte[], byte[]> hgetAll = jedis.hgetAll(SafeEncoder.encode(key));
			logger.debug("hgetAll key:" + key);
			return decodeMap(hgetAll);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw e;
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	private Map<byte[], byte[]> encodeMap(final Map<String, Serializable> values) {
		if (isEmpty(values)) {
			return Collections.emptyMap();
		}
		Map<String, Serializable> copy = new HashMap<String, Serializable>(values);
		Iterator<Entry<String, Serializable>> iterator = copy.entrySet().iterator();
		Map<byte[], byte[]> ret = new HashMap<byte[], byte[]>();
		while (iterator.hasNext()) {
			Entry<String, Serializable> next = iterator.next();
			ret.put(SafeEncoder.encode(next.getKey()), serialize(next.getValue()));
		}

		return ret;
	}

	public void hmSet(String key, Map<String, Serializable> values) throws Exception {
		Jedis jedis = null;
		try {
			jedis = this.jedisPool.getResource();
			jedis.hmset(SafeEncoder.encode(key), encodeMap(values));
			logger.debug("hmSet key:" + key + ", field:" + values.keySet());
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw e;
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	private byte[][] encodeArray(final String[] array) {
		if (ArrayUtils.isEmpty(array)) {
			return new byte[0][0];
		}
		int len = array.length;
		List<byte[]> list = new ArrayList<byte[]>(len);
		for (int i = 0; i < len; i++) {
			list.add(SafeEncoder.encode(array[i]));
		}
		return list.toArray(new byte[len][0]);
	}

	public List<Object> hmGet(String key, String... fields) throws Exception {
		Jedis jedis = null;
		try {
			jedis = this.jedisPool.getResource();
			List<byte[]> hmget = jedis.hmget(SafeEncoder.encode(key), encodeArray(fields));
			logger.debug("hmGet key:" + key + ", fields:" + Arrays.toString(fields));
			if (isEmpty(hmget)) {
				return new ArrayList<Object>(1);
			} else {
				List<Object> ret = new ArrayList<Object>(hmget.size());
				for (byte[] bb : hmget) {
					ret.add(deserialize(bb));
				}
				return ret;
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw e;
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	public List<String> hmGetByStringSerializer(String key, String... fields) throws Exception {
		Jedis jedis = null;
		try {
			jedis = this.jedisPool.getResource();
			List<String> hmget = jedis.hmget(key, fields);
			logger.debug("hmGet key:" + key + ", fields:" + Arrays.toString(fields));
			if (isEmpty(hmget)) {
				return new ArrayList<String>(1);
			} else {
				return hmget;
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw e;
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	public void hmSetByStringSerializer(String key, Map<String, String> values) throws Exception {
		Jedis jedis = null;
		try {
			jedis = this.jedisPool.getResource();
			if (isEmpty(values)) {
				values = Collections.emptyMap();
			}
			jedis.hmset(key, values);
			// LOG.info("hmSet key:" + key + " field:" + values.keySet());
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw e;
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	public boolean sAdd(String key, String member) {
		Jedis jedis = null;
		try {
			jedis = this.jedisPool.getResource();
			Long r = jedis.sadd(key, member);
			return r == 1;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw e;
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	public boolean sRem(String key, String member) {
		Jedis jedis = null;
		try {
			jedis = this.jedisPool.getResource();
			Long r = jedis.srem(key, member);
			return r == 1;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw e;
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	public String sPop(String key) {
		Jedis jedis = null;
		try {
			jedis = this.jedisPool.getResource();
			return jedis.spop(key);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw e;
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	public Set<String> sMembers(String key) {
		Jedis jedis = null;
		try {
			jedis = this.jedisPool.getResource();
			Set<String> out = jedis.smembers(key);

			return out;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw e;
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	public boolean zadd(String key, double score, String member) {
		Jedis jedis = null;
		try {
			jedis = this.jedisPool.getResource();
			Long r = jedis.zadd(key, score, member);
			return r == 1;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw e;
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	public boolean zrem(String key, String member) {
		Jedis jedis = null;
		try {
			jedis = this.jedisPool.getResource();
			Long r = jedis.zrem(key, member);
			return r == 1;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw e;
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	public long zcount(String key, String min, String max) {
		Jedis jedis = null;
		try {
			jedis = this.jedisPool.getResource();
			return jedis.zcount(key, min, max);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw e;
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	public Set<String> zrange(String key, long start, long end) {
		Jedis jedis = null;
		try {
			jedis = this.jedisPool.getResource();
			return jedis.zrange(key, start, end);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw e;
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	public static final String MIN_STR = "-inf";
	public static final String MAX_STR = "+inf";
	public Set<String> zrevrange(String key, long start, long end) {
		Jedis jedis = null;
		try {
			jedis = this.jedisPool.getResource();
			return jedis.zrevrange(key, start, end);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw e;
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	public Set<String> zrangeByScore(String key, String min, String max, int offset, int count) {
		Jedis jedis = null;
		try {
			jedis = this.jedisPool.getResource();
			return jedis.zrangeByScore(key, min, max, offset, count);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw e;
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	public Set<String> zrevrangeByScore(String key, String max, String min, int offset, int count) {
		Jedis jedis = null;
		try {
			jedis = this.jedisPool.getResource();
			return jedis.zrevrangeByScore(key, max, min, offset, count);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw e;
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	public boolean lpush(String key, Object value) throws Exception {
		Jedis jedis = null;
		try {
			jedis = this.jedisPool.getResource();

			// long begin = System.currentTimeMillis();
			jedis.lpush(SafeEncoder.encode(key), jsonSerialize(value));
			// long end = System.currentTimeMillis();
			// LOG.info("lpush key:" + key + " spends: " + (end - begin) + " millionseconds.");
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw e;
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
		return true;
	}

	public Object lpop(String key, Class<?> cls) throws Exception {
		byte[] data = null;
		Jedis jedis = null;
		try {
			jedis = this.jedisPool.getResource();
			// long begin = System.currentTimeMillis();
			data = jedis.lpop(SafeEncoder.encode(key));
			// long end = System.currentTimeMillis();
			// LOG.info("getValueFromCache spends: " + (end - begin) + " millionseconds.");
		} catch (Exception e) {
			// do jedis.quit() and jedis.disconnect()
			throw e;
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}

		return this.jsonDeserialize(data, cls);
	}

	public boolean rpush(String key, Object value) throws Exception {
		Jedis jedis = null;
		try {
			jedis = this.jedisPool.getResource();

			// long begin = System.currentTimeMillis();
			jedis.rpush(SafeEncoder.encode(key), jsonSerialize(value));
			// long end = System.currentTimeMillis();
			// LOG.info("rpush key:" + key + " spends: " + (end - begin) + " millionseconds.");
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw e;
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
		return true;
	}

	public Object rpop(String key, Class<?> cls) throws Exception {
		byte[] data = null;
		Jedis jedis = null;
		try {
			jedis = this.jedisPool.getResource();
			// long begin = System.currentTimeMillis();
			data = jedis.rpop(SafeEncoder.encode(key));
			// long end = System.currentTimeMillis();
			// LOG.info("getValueFromCache spends: " + (end - begin) + " millionseconds.");
		} catch (Exception e) {
			// do jedis.quit() and jedis.disconnect()
			throw e;
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}

		return this.jsonDeserialize(data, cls);
	}

	public Long incr(String key) throws Exception {
		Long data = null;
		Jedis jedis = null;
		try {
			jedis = this.jedisPool.getResource();
			// long begin = System.currentTimeMillis();
			data = jedis.incr(key);
			// long end = System.currentTimeMillis();
			// LOG.info("getValueFromCache spends: " + (end - begin) + " millionseconds.");
		} catch (Exception e) {
			// do jedis.quit() and jedis.disconnect()
			throw e;
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
		return data;
	}

	public Long incrBy(final String key, final long integer) throws Exception {
		Long data = null;
		Jedis jedis = null;
		try {
			jedis = this.jedisPool.getResource();
			// long begin = System.currentTimeMillis();
			data = jedis.incrBy(key, integer);
			// long end = System.currentTimeMillis();
			// LOG.info("getValueFromCache spends: " + (end - begin) + " millionseconds.");
		} catch (Exception e) {
			// do jedis.quit() and jedis.disconnect()
			throw e;
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}

		return data;
	}

	/**
	 * Set key to hold string value if key does not exist. In that case, it is equal to SET.
	 * When key already holds a value, no operation is performed. SETNX is short for "SET if N ot e X ists".
	 *
	 * @param key        Key to be operated.
	 * @param value      Value to be set.
	 * @param expiration Expiration time
	 * @return 1 if the key was set, 0 if hte key was not set.
	 * @throws Exception if execute failed.
	 * @see <a href="http://redis.io/commands/setnx">Redis: SETNX</a>
	 */
	public Long setnx(String key, Object value, int expiration) throws Exception {
		Long result = 0L;
		Jedis jedis = null;
		try {
			jedis = this.jedisPool.getResource();
			result = jedis.setnx(SafeEncoder.encode(key), serialize(value));
			jedis.expire(SafeEncoder.encode(key), expiration);
			return result;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw e;
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	/**
	 * Get the bytes representing the given serialized object.
	 */
	protected byte[] jsonSerialize(Object o) {
		byte[] res = null;
		try {
			res = JsonMapper.toJsonString(o).getBytes("utf-8");
		} catch (UnsupportedEncodingException e) {
			logger.error("JsonSerialize object fail ", e);
		}
		return res;
	}

	/**
	 * Get the object represented by the given serialized bytes.
	 */
	protected Object jsonDeserialize(byte[] in, Class<?> cls) {
		if (in == null || in.length == 0) {
			return null;
		}
		Object res = null;
		try {
			res = JsonMapper.fromJsonString(new String(in, "utf-8"), cls);
		} catch (UnsupportedEncodingException e) {
			logger.error("DeSerialize object fail ", e);
		}
		return res;
	}

	public void destroy() throws Exception {
		this.jedisPool.destroy();

	}

	public String getRedisServerHost() {
		return redisServerHost;
	}

	public void setRedisServerHost(String redisServerHost) {
		this.redisServerHost = redisServerHost;
	}

	public String getRedisAuthKey() {
		return redisAuthKey;
	}

	public void setRedisAuthKey(String redisAuthKey) {
		this.redisAuthKey = redisAuthKey;
	}

	public String getCacheName() {
		return cacheName;
	}

	public void setCacheName(String cacheName) {
		this.cacheName = cacheName;
	}

	public int getMaxIdle() {
		return maxIdle;
	}

	public void setMaxIdle(int maxIdle) {
		this.maxIdle = maxIdle;
	}

	public int getRedisServerPort() {
		return redisServerPort;
	}

	public void setRedisServerPort(int redisServerPort) {
		this.redisServerPort = redisServerPort;
	}

	/**
	 * @return the testOnBorrow
	 */
	public boolean isTestOnBorrow() {
		return testOnBorrow;
	}

	/**
	 * @param testOnBorrow the testOnBorrow to set
	 */
	public void setTestOnBorrow(boolean testOnBorrow) {
		this.testOnBorrow = testOnBorrow;
	}

	/**
	 * @return the minIdle
	 */
	public int getMinIdle() {
		return minIdle;
	}

	/**
	 * @param minIdle the minIdle to set
	 */
	public void setMinIdle(int minIdle) {
		this.minIdle = minIdle;
	}

	public long getMaxWaitMillis() {
		return maxWaitMillis;
	}

	public void setMaxWaitMillis(long maxWaitMillis) {
		this.maxWaitMillis = maxWaitMillis;
	}

	public boolean isBlockWhenExhausted() {
		return blockWhenExhausted;
	}

	public void setBlockWhenExhausted(boolean blockWhenExhausted) {
		this.blockWhenExhausted = blockWhenExhausted;
	}

	/**
	 * @return the testOnReturn
	 */
	public boolean isTestOnReturn() {
		return testOnReturn;
	}

	/**
	 * @param testOnReturn the testOnReturn to set
	 */
	public void setTestOnReturn(boolean testOnReturn) {
		this.testOnReturn = testOnReturn;
	}

	/**
	 * @return the testWhileIdle
	 */
	public boolean isTestWhileIdle() {
		return testWhileIdle;
	}

	/**
	 * @param testWhileIdle the testWhileIdle to set
	 */
	public void setTestWhileIdle(boolean testWhileIdle) {
		this.testWhileIdle = testWhileIdle;
	}

	/**
	 * @return the timeBetweenEvictionRunsMillis
	 */
	public long getTimeBetweenEvictionRunsMillis() {
		return timeBetweenEvictionRunsMillis;
	}

	/**
	 * @param timeBetweenEvictionRunsMillis the timeBetweenEvictionRunsMillis to set
	 */
	public void setTimeBetweenEvictionRunsMillis(long timeBetweenEvictionRunsMillis) {
		this.timeBetweenEvictionRunsMillis = timeBetweenEvictionRunsMillis;
	}

	/**
	 * @return the numTestsPerEvictionRun
	 */
	public int getNumTestsPerEvictionRun() {
		return numTestsPerEvictionRun;
	}

	/**
	 * @param numTestsPerEvictionRun the numTestsPerEvictionRun to set
	 */
	public void setNumTestsPerEvictionRun(int numTestsPerEvictionRun) {
		this.numTestsPerEvictionRun = numTestsPerEvictionRun;
	}

	/**
	 * @return the minEvictableIdleTimeMillis
	 */
	public long getMinEvictableIdleTimeMillis() {
		return minEvictableIdleTimeMillis;
	}

	/**
	 * @param minEvictableIdleTimeMillis the minEvictableIdleTimeMillis to set
	 */
	public void setMinEvictableIdleTimeMillis(long minEvictableIdleTimeMillis) {
		this.minEvictableIdleTimeMillis = minEvictableIdleTimeMillis;
	}

	/**
	 * @return the softMinEvictableIdleTimeMillis
	 */
	public long getSoftMinEvictableIdleTimeMillis() {
		return softMinEvictableIdleTimeMillis;
	}

	/**
	 * @param softMinEvictableIdleTimeMillis the softMinEvictableIdleTimeMillis to set
	 */
	public void setSoftMinEvictableIdleTimeMillis(long softMinEvictableIdleTimeMillis) {
		this.softMinEvictableIdleTimeMillis = softMinEvictableIdleTimeMillis;
	}

	/**
	 * @return the lifo
	 */
	public boolean isLifo() {
		return lifo;
	}

	/**
	 * @param lifo the lifo to set
	 */
	public void setLifo(boolean lifo) {
		this.lifo = lifo;
	}

	public String getLiteralRedisServer() {
		return redisServerHost + ":" + redisServerPort;
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	public boolean isAlive() {
		return isAlive;
	}

	public void setAlive(boolean isAlive) {
		this.isAlive = isAlive;
	}

	public int getTimeout() {
		return timeout;
	}


	private boolean isEmpty(Collection collection) {
		return null == collection || collection.size() < 1;
	}

	private boolean isEmpty(Map map) {
		return null == map || map.size() < 1;
	}
}
