package cluster;

import com.jingcai.apps.common.jdbc.cache.redis.JedisClient;
import com.jingcai.apps.common.jdbc.cache.redis.JedisUtils;
import org.junit.Test;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.io.Serializable;
import java.util.Set;

/**
 * Created by lejing on 16/4/22.
 */
public class JedisUtilsTest {
	private JedisUtils jedisClient = getJedisClient();

	@Test
	public void test1(){
		Integer obj = new Integer(10);
		Integer obj2 = new Integer(11);
		jedisClient.hset("aaa", "f1", obj);
		jedisClient.hset("aaa", "f2", obj2);
		Set<String> keys = jedisClient.hkeys("aaa");
		for (String key : keys) {
			System.out.println(key);
			System.out.println(jedisClient.hget("aaa", key));
		}
		System.out.println(jedisClient.exists("aaa"));
		System.out.println(jedisClient.exists("aaa2"));
		System.out.println(jedisClient.hget("aaa", "f3"));
	}

	@Test
	public void test2(){
		Set<String> keys = jedisClient.hkeys("activityvcount");
		for (String key : keys) {
			System.out.println(key);
			System.out.println("\t\t"+jedisClient.hget("activityvcount", key));
		}
	}

	private JedisUtils getJedisClient() {
		JedisPoolConfig config = new JedisPoolConfig();
		config.setMaxIdle(300);
		config.setMaxTotal(60000);
		config.setMaxWaitMillis(10000);
		config.setTestOnBorrow(true);

//		JedisPool pool = new JedisPool(config, "192.168.0.11", 6379, 10000);
		JedisPool pool = new JedisPool(config, "202.204.49.215", 6379, 10000);
		JedisUtils jedisUtils = new JedisUtils(pool, "qd");
		return jedisUtils;
	}
}
class Obj implements Serializable {
	private String f1;
	private String f2;
}
