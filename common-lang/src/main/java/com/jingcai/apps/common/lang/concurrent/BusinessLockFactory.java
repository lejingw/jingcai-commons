package com.jingcai.apps.common.lang.concurrent;

import org.springframework.beans.factory.FactoryBean;

/**
 * Created by lejing on 15/10/27.
 */
public class BusinessLockFactory implements FactoryBean<BusinessLock>{

	public BusinessLock getObject() throws Exception {
		return new BusinessLock();
	}

	public Class<?> getObjectType() {
		return BusinessLock.class;
	}

	public boolean isSingleton() {
		return false;
	}
}
