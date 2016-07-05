package com.jingcai.apps.common.ice;

import Ice.*;
import Ice.Object;
import IceBox.Service;

import java.util.Arrays;

/**
 * Created by lejing on 16/7/4.
 */
public abstract class AbstractIceBoxService implements Service {
	private static Logger logger;
	protected ObjectAdapter adapter;
	private Identity id;

	public void start(String name, Communicator communicator, String[] args) {
		if(null == logger) {
			logger = communicator.getLogger();
		}
		//String identity = communicator().getProperties().getProperty("AdvmasterFacade.Identity");
		adapter = communicator.createObjectAdapter(name);
		Object obj = this.createIceServiceObj(args);
		id = communicator.stringToIdentity(name + "Facade");
		adapter.add(PerfDispatchInterceptor.addIceObject(id, obj), id);
		adapter.activate();
		logger.print("==== " + name + " service started ==== with params:" + Arrays.toString(args));
	}

	protected abstract Object createIceServiceObj(String[] args);

	public void stop() {
		adapter.destroy();
		PerfDispatchInterceptor.removeIceObject(id);
		logger.print("==== " + id + " service stopped ====");
	}
}
