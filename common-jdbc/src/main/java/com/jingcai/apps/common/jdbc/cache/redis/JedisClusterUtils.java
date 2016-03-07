package com.jingcai.apps.common.jdbc.cache.redis;

import com.google.common.collect.Lists;
import com.jingcai.apps.common.lang.serialize.KryoUtils;
import com.jingcai.apps.common.lang.serialize.ObjectUtils;
import com.jingcai.apps.common.lang.string.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.params.set.SetParams;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by lejing on 16/1/14.
 */
@Slf4j
public class JedisClusterUtils {
	public static final String FORMAT = "%s-%s";
	private JedisCluster2 jc;
	private String keyPrefix;
	private boolean useKyro;

	public JedisClusterUtils(String addrs, int timeout, int maxTotal, int maxIdle) {
		this(addrs, timeout, maxTotal, maxIdle, null);
	}

	public JedisClusterUtils(String addrs, int timeout, int maxTotal, int maxIdle, String keyPrefix) {
		this(addrs, timeout, maxTotal, maxIdle, keyPrefix, false);
	}

	public JedisClusterUtils(String addrs, int timeout, int maxTotal, int maxIdle, String keyPrefix, boolean useKyro) {
		Set<HostAndPort> jedisClusterNodes = new HashSet<HostAndPort>();
		String[] addrArr = addrs.split("[,\\s;]");
		String[] ipAndPort = null;
		for (String addr : addrArr) {
			if (StringUtils.isNotEmpty(addr) && (2 == (ipAndPort = addr.split("[:]")).length)) {
				jedisClusterNodes.add(new HostAndPort(ipAndPort[0], Integer.parseInt(ipAndPort[1])));
			}
		}
		GenericObjectPoolConfig config = new GenericObjectPoolConfig();
		config.setMaxTotal(maxTotal);
		config.setMaxIdle(maxIdle);
		this.jc = new JedisCluster2(jedisClusterNodes, timeout, config);
		this.keyPrefix = keyPrefix;
		this.useKyro = useKyro;
	}

	public JedisClusterUtils(Set<HostAndPort> jedisClusterNodes, int timeout, int maxTotal, int maxIdle) {
		this(jedisClusterNodes, timeout, maxTotal, maxIdle, null);
	}

	public JedisClusterUtils(Set<HostAndPort> jedisClusterNodes, int timeout, int maxTotal, int maxIdle, String keyPrefix) {
		GenericObjectPoolConfig config = new GenericObjectPoolConfig();
		config.setMaxTotal(maxTotal);
		config.setMaxIdle(maxIdle);
		jc = new JedisCluster2(jedisClusterNodes, timeout, config);
		this.keyPrefix = keyPrefix;
	}

	public void set(String key, String value, int timeInSeconds) {
		key = getKey(key);
		if (timeInSeconds <= 0) {
			jc.set(key, value);
		} else {
			jc.set(key, value, SetParams.setParams().ex(timeInSeconds));
		}
	}

	public String get(String key) {
		String value = null;
		key = getKey(key);
		if (jc.exists(key)) {
			value = jc.get(key);
			value = StringUtils.isNotBlank(value) && !"nil".equalsIgnoreCase(value) ? value : null;
			log.debug("get {} = {}", key, value);
		}
		return value;
	}

	public void set(String key, Object object, int timeInSeconds) {
		if (null == object) return;
		key = getKey(key);
		final byte[] keyBytes = key.getBytes();
		byte[] bytes = getBytes(object);
		if (timeInSeconds <= 0) {
			jc.set(keyBytes, bytes);
		} else {
			jc.set(keyBytes, bytes, SetParams.setParams().ex(timeInSeconds));
		}
	}

	public Object getObject(String key) {
		key = getKey(key);
		byte[] keyBytes = key.getBytes();
		Object value = null;
		if (jc.exists(keyBytes)) {
			byte[] bytes = jc.getBytes(keyBytes);
			if (null != bytes && bytes.length > 0) {
				value = getObject(bytes);
			}
			log.debug("get {} = {}", key, value);
		}
		return value;
	}

	public void hset(String key, String field, Object object) {
		if (null == object) return;
		key = getKey(key);
		final byte[] keyBytes = key.getBytes();
		final byte[] fieldBytes = field.getBytes();
		byte[] bytes = getBytes(object);
		log.debug("set key:{} field:{} = {}", key, field, object);
		jc.hset(keyBytes, fieldBytes, bytes);
	}

	public Object hget(String key, String field) {
		key = getKey(key);
		final byte[] keyBytes = key.getBytes();
		final byte[] fieldBytes = field.getBytes();
		byte[] bytes = jc.hget(keyBytes, fieldBytes);
		Object value = null;
		if (null != bytes && bytes.length > 0) {
			value = KryoUtils.getKryo().readClassAndObject(bytes);
		}
		log.debug("get key:{} field:{} = {}", key, field, value);
		return value;
	}

	public void hdel(String key, String field) {
		key = getKey(key);
		final byte[] keyBytes = key.getBytes();
		final byte[] fieldBytes = field.getBytes();
		jc.hdel(keyBytes, fieldBytes);
		log.debug("del key:{} field:{}", key, field);
	}

	public long hlen(String key) {
		key = getKey(key);
		final byte[] keyBytes = key.getBytes();
		Long hlen = jc.hlen(keyBytes);
		log.debug("length key:{} = {}", key, hlen);
		return hlen;
	}

	public String getKey(String key) {
		if (null == keyPrefix) {
			return key;
		}
		return String.format(FORMAT, keyPrefix, key);
	}

	public boolean exists(String key) {
		key = getKey(key);
		final byte[] keyBytes = key.getBytes();
		return jc.exists(keyBytes);
	}

	public void delete(String key) {
		key = getKey(key);
		log.debug("del key:{}", key);
		jc.del(key.getBytes());
	}

	public void delete(String... keys) {
		List<byte[]> list = Lists.newArrayListWithCapacity(keys.length);
		for (String key : keys) {
			list.add(getKey(key).getBytes());
		}
		jc.del((byte[][]) list.toArray());
	}

	private byte[] getBytes(Object object) {
		if (useKyro) {
			return KryoUtils.getKryo().writeClassAndObject(object);
		} else {
			return ObjectUtils.serialize(object);
		}
	}

	private Object getObject(byte[] bytes) {
		if (useKyro) {
			return KryoUtils.getKryo().readClassAndObject(bytes);
		} else {
			return ObjectUtils.unserialize(bytes);
		}
	}
}