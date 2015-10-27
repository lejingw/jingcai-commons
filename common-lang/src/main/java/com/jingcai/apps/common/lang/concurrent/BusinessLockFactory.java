package com.jingcai.apps.common.lang.concurrent;

import org.springframework.beans.factory.FactoryBean;

/**
 * Created by lejing on 15/10/27.
 */
public class BusinessLockFactory implements FactoryBean<BusinessLock> {
	private int maxSize = -1;

	public BusinessLock getObject() throws Exception {
		if (maxSize > 0) {
			return new BusinessLock(maxSize);
		}
		return new BusinessLock();
	}

	public Class<?> getObjectType() {
		return BusinessLock.class;
	}

	public boolean isSingleton() {
		return false;
	}

	public void setMaxSize(int maxSize) {
		this.maxSize = maxSize;
	}
}
