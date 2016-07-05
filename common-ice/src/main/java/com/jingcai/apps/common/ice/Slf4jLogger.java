package com.jingcai.apps.common.ice;

import Ice.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by lejing on 16/7/4.
 */
public class Slf4jLogger implements Logger {
	private org.slf4j.Logger logger;
	private String prefix;

	public Slf4jLogger(String loggerName) {
		prefix = loggerName;
		logger = LoggerFactory.getLogger(loggerName);
	}

	public void print(String message) {
		logger.info(message);
	}

	public void trace(String catalog, String message) {
		logger.debug(catalog + " " + message);
	}

	public void warning(String message) {
		logger.warn(message);
	}

	public void error(String message) {
		logger.error(message);
	}

	public String getPrefix() {
		return prefix;
	}

	public Logger cloneWithPrefix(String pre) {
		return new Slf4jLogger(pre);
	}
}
