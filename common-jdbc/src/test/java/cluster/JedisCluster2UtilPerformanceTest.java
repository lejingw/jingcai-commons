package cluster;

import com.jingcai.apps.common.jdbc.cache.redis.JedisClusterUtils;
import org.junit.Test;
import redis.clients.jedis.HostAndPort;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * Created by lejing on 16/1/14.
 */
public class JedisCluster2UtilPerformanceTest {
	private JedisClusterUtils util = getJedisClusterUtils();
	private int time = 60 * 60 * 1;//1h

	@Test
	public void testAddCommonObj() throws InterruptedException {
		String key1 = "key1";
		assertNull(util.get(key1));

		Student val1 = getStudent();
		util.set(key1, val1, time);
		assertNotNull(util.get(key1));
	}
	@Test
	public void testDeleteCommonObj(){
		String key1 = "key1";
		util.delete(key1);
		assertNull(util.get(key1));
	}

	private Student getStudent() {
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
		val1.setSalary(9.999);
		return val1;
	}

	int count = 1000000;
	@Test
	public void testAdd(){
		Student val1 = getStudent();//288 byte
		long start = System.currentTimeMillis();
		for(int i=0;i<count;i++){
			val1.setName1("admin" +i);
			util.set("key" + i, val1, time);
		}
		System.out.println("cost time:" + (System.currentTimeMillis() - start) + "ms/" + count);
	}
	@Test
	public void testDelete(){
		long start = System.currentTimeMillis();
		for(int i=0;i<count;i++){
			util.delete("key" + i);
		}
		System.out.println("cost time:" + (System.currentTimeMillis() - start) + "ms/" + count);
	}
	//10000		1045232			104.5232		5057	ms/10000		0.996M
	//100000	11801104		118.0110		4064	ms/10000		11.8M
	//1000000	116,739,000		116.729			4107	ms/10000		111.33M



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

	@Test
	public void testSplit(){
		String adds = "aaa,bbb ccc;ddd";
		String[] split = adds.split("[,\\s;]");
		for (String s : split) {
			System.out.println(s);
		}
	}

	@Test
	public void testFormat(){
		String format = "%s_%s";
		System.out.println(String.format(format, null, "abcd"));
	}
}