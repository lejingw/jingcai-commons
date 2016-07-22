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
		String key = "key1";
		String val = "hello world!";
		Object o = redisMgr.get(key);
		assertThat(o).isNull();

		String put = redisMgr.put(key, 100, val);
		System.out.println(put);

		o = redisMgr.get(key);
		assertThat(o).isEqualTo(val);

		redisMgr.remove(key);
	}
}
