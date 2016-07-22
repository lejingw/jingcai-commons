package cluster;

import com.jingcai.apps.common.jdbc.cache.redis.JedisClusterUtils;
import org.junit.Test;
import redis.clients.jedis.HostAndPort;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Created by lejing on 16/1/14.
 */
public class JedisCluster2UtilTest {
	private JedisClusterUtils util = getJedisClusterUtils();
	@Test
	public void test_string() throws InterruptedException {
		String key1 = "key11";

		assertNull(util.get(key1));

		String val1 = "value1";
		util.set(key1, val1, 1);
		assertEquals(val1, util.get(key1));
		Thread.sleep(1000);
		assertNull(util.get(key1));
	}

	@Test
	public void test_delete() throws InterruptedException {
		String key1 = "key1";
		assertNull(util.get(key1));

		String val1 = "value1";
		util.set(key1, val1, 1000);
		util.delete(key1);
		assertNull(util.get(key1));
	}
	@Test
	public void test_delete2() throws InterruptedException {
		String key1 = "key1";
		assertNull(util.get(key1));

		Student val1 = new Student();
		val1.setName("admin0");
		val1.setName1("admin1");
		val1.setName2("admin2");
		val1.setName3("admin3");
		val1.setName4("admin4");
		val1.setName5("admin5");
		val1.setName6("admin6");
		val1.setName7("admin7");
		val1.setName8("admin8");
		val1.setName9("admin9");
		val1.setName10("admin10");
		val1.setGender("male");
		val1.setAge(9);
		util.set(key1, val1, 1000);
		util.delete(key1);
		assertNull(util.get(key1));
	}
	@Test
	public void test_object() throws InterruptedException {
		String key1 = "key2";

		assertNull(util.get(key1));

		Student val1 = new Student();
		val1.setName("admin0");
		val1.setName1("admin1");
		val1.setName2("admin2");
		val1.setName3("admin3");
		val1.setName4("admin4");
		val1.setName5("admin5");
		val1.setName6("admin6");
		val1.setName7("admin7");
		val1.setName8("admin8");
		val1.setName9("admin9");
		val1.setName10("admin10");
		val1.setGender("male");
		val1.setAge(9);
		util.set(key1, val1, 1);

		Student stu = (Student) util.getObject(key1);
		assertEquals(val1.getName(), stu.getName());
		assertEquals(val1.getGender(), stu.getGender());
		assertEquals(val1.getAge(), stu.getAge());
		Thread.sleep(1000);
		assertNull(util.get(key1));
	}

	@Test
	public void test_hset(){
		String key = "hkey1";
		String field = "field1";
		String val = "abc2";
		util.hset(key, field, val);

		String val2 = (String) util.hget(key, field);
		assertEquals(val, val2);
		util.hdel(key, field);
		long hlen = util.hlen(key);
		assertEquals(0, hlen);

	}


	@Test
	public void testIncr(){
		String key = "key123";
		util.set(key, "10", 10);
		util.incr(key);
		util.incrBy(key, 8);
		String s = util.get(key);
		int count = Integer.valueOf(s);
		System.out.println(count);
	}

	private JedisClusterUtils getJedisClusterUtils() {
		Set<HostAndPort> jedisClusterNodes = new HashSet<HostAndPort>();
		//Jedis Cluster will attempt to discover cluster nodes automatically
		{
			jedisClusterNodes.add(new HostAndPort("192.168.0.19", 7000));
			jedisClusterNodes.add(new HostAndPort("192.168.0.19", 7001));
			jedisClusterNodes.add(new HostAndPort("192.168.0.19", 7002));
			jedisClusterNodes.add(new HostAndPort("192.168.0.19", 7003));
			jedisClusterNodes.add(new HostAndPort("192.168.0.19", 7004));
			jedisClusterNodes.add(new HostAndPort("192.168.0.19", 7006));
		}

		return new JedisClusterUtils(jedisClusterNodes, 5000, 10, 2);
	}
	private JedisClusterUtils getJedisClusterUtils2() {
//		String addrs = "192.168.0.19:7000,192.168.0.19:7001,192.168.0.19:7002,192.168.0.19:7003,192.168.0.19:7004,192.168.0.19:7006";
//		String addrs = "192.168.0.11:6379";
		String addrs = "101.200.159.181:6380,101.200.159.181:6381,101.200.159.228:6380,101.200.159.228:6381,101.200.186.179:6380,101.200.186.179:6381";
//		String addrs = "101.200.159.181:6380,101.200.159.228:6380,101.200.186.179:6380";
		return new JedisClusterUtils(addrs, 5000, 10, 2);
	}
}