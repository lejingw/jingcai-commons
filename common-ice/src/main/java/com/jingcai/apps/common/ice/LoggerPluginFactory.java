package com.jingcai.apps.common.ice;

import Ice.Communicator;
import Ice.Plugin;
import Ice.PluginFactory;

/**
 * Created by lejing on 16/7/5.
 */
public class LoggerPluginFactory implements PluginFactory {
	@Override
	public Plugin create(Communicator communicator, String s, String[] strings) {
		return new Ice.LoggerPlugin(communicator, new Slf4jLogger("ICE-SYSTEM"));
	}
}
