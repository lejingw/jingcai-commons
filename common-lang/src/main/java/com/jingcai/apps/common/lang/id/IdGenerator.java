package com.jingcai.apps.common.lang.id;

import com.jingcai.apps.common.lang.string.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.imps.CuratorFrameworkState;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by lejing on 16/1/5.
 */
@Slf4j
public class IdGenerator {
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

	public int id(Class cls) {
		IdGenEntry idGenentry = getIdGenentry(cls);
		return idGenentry.nextId();
	}

	public String nextId(Class cls) {
		return String.valueOf(id(cls));
	}

	public void init() {
		if (StringUtils.isEmpty(connectString)) {
			log.error("connectString can't be empty");
			return;
		}
		if (StringUtils.isEmpty(prefix)) {
			log.error("prefix can't be empty");
			return;
		}
		if (stepLength <= 0) {
			log.error("stepLength should be positive");
			return;
		}
		curatorFramework = CuratorFrameworkFactory.newClient(connectString, new ExponentialBackoffRetry(1000, 3));
		curatorFramework.start();
	}

	public void destroy(){
		if(null == curatorFramework)	return;
		if(curatorFramework.getState() == CuratorFrameworkState.STARTED){
			curatorFramework.close();
		}
		curatorFramework = null;
	}

	public void setCuratorFramework(CuratorFramework curatorFramework) {
		this.curatorFramework = curatorFramework;
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
