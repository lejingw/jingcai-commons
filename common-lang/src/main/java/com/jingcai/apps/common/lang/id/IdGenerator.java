package com.jingcai.apps.common.lang.id;

import com.jingcai.apps.common.lang.string.StringUtils;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by lejing on 16/1/5.
 */
public class IdGenerator implements InitializingBean {
	private static final Logger log = LoggerFactory.getLogger(IdGenerator.class);
	private static final String PATH_PATTERN = "%s/idlocks/%s";
	private Map<Class, IdGenEntry> map = new ConcurrentHashMap<Class, IdGenEntry>(3);
	private String connectString;
	private String prefix;
	private int stepLength = 10;
	private CuratorFramework curatorFramework;

	private IdGenEntry getIdGenentry(Class cls) {
		if (map.containsKey(cls)) {
			return map.get(cls);
		}
		String lockPath = String.format(PATH_PATTERN, prefix, cls.getSimpleName());
		IdGenEntry idGenEntry = new IdGenEntry(curatorFramework, lockPath, stepLength);
		map.put(cls, idGenEntry);
		return idGenEntry;
	}

	public int nextId(Class cls){
		IdGenEntry idGenentry = getIdGenentry(cls);
		return idGenentry.nextId();
	}

	public void afterPropertiesSet() throws Exception {
		init();
	}

	public void init() {
		if(StringUtils.isEmpty(connectString)){
			log.error("connectString can't be empty");
			return;
		}
		if(StringUtils.isEmpty(prefix)){
			log.error("prefix can't be empty");
			return;
		}
		if(stepLength<=0){
			log.error("stepLength should be positive");
			return;
		}
		curatorFramework = CuratorFrameworkFactory.newClient(connectString, new ExponentialBackoffRetry(1000, 3));
		curatorFramework.start();
	}

	public void setStepLength(int stepLength) {
		this.stepLength = stepLength;
	}

	public void setConnectString(String connectString) {
		this.connectString = connectString;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}
}
