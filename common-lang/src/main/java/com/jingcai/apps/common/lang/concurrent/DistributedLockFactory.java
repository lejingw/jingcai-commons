package com.jingcai.apps.common.lang.concurrent;

import com.jingcai.apps.common.lang.string.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.FactoryBean;

/**
 * Created by lejing on 15/10/27.
 */
public class DistributedLockFactory implements FactoryBean<DistributedLock> {
	private static final Logger logger = LoggerFactory.getLogger(DistributedLock.class);
	private String address, lockname;

	public DistributedLock getObject() throws Exception {
		if(StringUtils.isEmpty(address) || StringUtils.isEmpty(lockname)){
			logger.error("!!!init DistributedLock error, address and lockname should be set!!!");
		}
		return new DistributedLock(address, lockname);
	}

	public Class<?> getObjectType() {
		return DistributedLock.class;
	}

	public boolean isSingleton() {
		return false;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public void setLockname(String lockname) {
		this.lockname = lockname;
	}
}
