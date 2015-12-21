package com.jingcai.apps.common.lang.concurrent;

import com.jingcai.apps.common.lang.string.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by lejing on 15/10/27.
 */
public class DistributedLockGen {
	private static final Logger logger = LoggerFactory.getLogger(DistributedLock.class);
	private String address;

	public DistributedLock get(String lockname) throws Exception {
		if(StringUtils.isEmpty(address) || StringUtils.isEmpty(lockname)){
			logger.error("!!!init DistributedLock error, address and lockname should be set!!!");
			throw new DistributedLock.LockException("could not generate DistributedLock");
		}
		return new DistributedLock(address, lockname);
	}

	public void setAddress(String address) {
		this.address = address;
	}
}
