package com.jingcai.apps.common.lang.id;

import java.util.UUID;

public class UuidGen {
	/**
	 * 封装JDK自带的UUID, 通过Random数字生成, 中间无-分割.
	 */
	public static String uuid() {
		return UUID.randomUUID().toString().replaceAll("-", "");
	}
}
