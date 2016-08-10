package redis;

import com.jingcai.apps.common.jdbc.redis.RedisCacheManager;
import com.jingcai.apps.common.jdbc.redis.RedisClient;
import com.jingcai.apps.common.jdbc.redis.config.RedisHAClientConfig;
import org.assertj.core.api.Assertions;
import org.junit.BeforeClass;
import org.junit.Test;

import java.nio.channels.Pipe;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lejing on 16/8/10.
 */
public class RedisCacheManagerTest {


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
	public void test1(){
		String key = "site";
		if(redisMgr.existsKey(key)){
			redisMgr.remove(key);
		}
		redisMgr.zadd(key, 1, "a");
		redisMgr.zadd(key, 2, "b");
		redisMgr.zadd(key, 3, "c");
		redisMgr.zadd(key, 4, "d");
		redisMgr.zadd(key, 5, "e");
		redisMgr.zrem(key, "e");

		long zcount = redisMgr.zcount(key, RedisClient.MIN_STR, RedisClient.MAX_STR);
		Assertions.assertThat(zcount).isEqualTo(4);
		zcount = redisMgr.zcount(key, RedisClient.MAX_STR, RedisClient.MIN_STR);
		Assertions.assertThat(zcount).isEqualTo(0);

		System.out.println(redisMgr.zrange(key, 1, 3));
		System.out.println(redisMgr.zrevrange(key, 1, 3));

		System.out.println(redisMgr.zrangeByScore(key, "1", "4", 1, 2));
		System.out.println(redisMgr.zrevrangeByScore(key, "4", "1", 0, 3));
	}
}
