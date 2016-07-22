package redis;

import com.jingcai.apps.common.jdbc.redis.RedisCacheManager;
import com.jingcai.apps.common.jdbc.redis.RedisClient;
import com.jingcai.apps.common.jdbc.redis.config.RedisHAClientConfig;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.assertj.core.api.Assertions.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lejing on 16/7/22.
 */
public class JedisTest {

	private static RedisCacheManager redisMgr;

	@BeforeClass
	public static void create() {
		redisMgr = RedisCacheManager.of(getRedisClientList()).buildRetryTimes(1)
				.buildEvictorCheckPeriodSeconds(10).buildEvictorDelayCheckSeconds(5).buildEvictorFailedTimesToBeTickOut(3);
	}

	private static List<RedisClient> getRedisClientList(){
		List<RedisClient> list = new ArrayList<>();
		{
			RedisHAClientConfig config = new RedisHAClientConfig();
			config.setRedisServerHost("djip");
			config.setRedisServerPort(6379);
			list.add(new RedisClient(config));
		}
		{
			RedisHAClientConfig config = new RedisHAClientConfig();
			config.setRedisServerHost("dja");
			config.setRedisServerPort(6379);
			list.add(new RedisClient(config));
		}
		return list;
	}

	@Test
	public void test1() throws InterruptedException {
		String key = "key3";
		String val = "111111";
		String o = redisMgr.get(key);
		assertThat(o).isNull();

		String put = redisMgr.put(key, 5, val);
		System.out.println(put);

		o = redisMgr.get(key);
		assertThat(o).isEqualTo(val);

		System.out.println(redisMgr.incr(key));
		System.out.println(redisMgr.incr(key));

		o = redisMgr.get(key);
		assertThat(o).isEqualTo("111113");

		//Thread.sleep(5 * 1000);
		redisMgr.remove(key);
	}

	@Test
	public void test_incr(){
		String key = "test_incr";
		if(redisMgr.existsKey(key)){
			System.out.println("============remove========");
			redisMgr.remove(key);
		}
		Object o = redisMgr.get(key);
		assertThat(o).isNull();

//		Long val = new Long(123);
		String val = "123";
		redisMgr.put(key, 10, val);

		o = redisMgr.get(key);
		System.out.println(redisMgr.incr(key));
		System.out.println(redisMgr.incrBy(key, 6));
	}
}
