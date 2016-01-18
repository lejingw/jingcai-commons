package com.jingcai.apps.common.jdbc.cache.redis;

import com.jingcai.apps.common.lang.serialize.KryoUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.params.set.SetParams;

import java.util.Set;

/**
 * Created by lejing on 16/1/14.
 */
@Slf4j
public class JedisClusterUtils {
	private JedisCluster2 jc;

	public JedisClusterUtils(Set<HostAndPort> jedisClusterNodes, int timeout, int maxTotal, int maxIdle) {
		GenericObjectPoolConfig config = new GenericObjectPoolConfig();
		config.setMaxTotal(maxTotal);
		config.setMaxIdle(maxIdle);
		jc = new JedisCluster2(jedisClusterNodes, timeout, config);
	}

	public void set(String key, String value, int timeInSeconds) {
		if (timeInSeconds <= 0) {
			jc.set(key, value);
		} else {
			jc.set(key, value, SetParams.setParams().ex(timeInSeconds));
		}
	}

	public void set(String key, Object object, int timeInSeconds) {
		if (null == object) return;
		final byte[] keyBytes = key.getBytes();
		byte[] bytes = KryoUtils.getKryo().writeClassAndObject(object);
		if (timeInSeconds <= 0) {
			jc.set(keyBytes, bytes);
		} else {
			jc.set(keyBytes, bytes, SetParams.setParams().ex(timeInSeconds));
		}
	}

	/**
	 * 获取缓存
	 *
	 * @param key 键
	 * @return 值
	 */
	public String get(String key) {
		try {
			if (jc.exists(key)) {
				String value = jc.get(key);
				value = StringUtils.isNotBlank(value) && !"nil".equalsIgnoreCase(value) ? value : null;
				log.debug("get {} = {}", key, value);
				return value;
			}
		} catch (Exception e) {
			log.warn("get {} error", key, e);
		}
		return null;
	}

	public void delete(String key) {
		jc.del(key.getBytes());
	}
	/*
	public void delete(String... keys){
		List<byte[]> list = Lists.newArrayListWithCapacity(keys.length);
		for(String key:keys){
			list.add(key.getBytes());
		}
		jc.del((byte[][]) list.toArray());
	}*/

	/**
	 * 获取缓存
	 *
	 * @param key 键
	 * @return 值
	 */
	public Object getObject(String key) {
		try {
			byte[] keyBytes = key.getBytes();
			if (jc.exists(keyBytes)) {
				byte[] bytes = jc.getBytes(keyBytes);
				Object value = null;
				if (null != bytes && bytes.length > 0) {
					value = KryoUtils.getKryo().readClassAndObject(bytes);
				}
				log.debug("get {} = {}", key, value);
				return value;
			}
		} catch (Exception e) {
			log.warn("get {} error", key, e);
		}
		return null;
	}
}