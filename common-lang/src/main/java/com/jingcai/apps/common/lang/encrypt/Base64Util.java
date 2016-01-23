package com.jingcai.apps.common.lang.encrypt;

import com.jingcai.apps.common.lang.exception.Exceptions;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

/**
 * Created by lejing on 16/1/23.
 */
public class Base64Util {

	/**
	 * BASE64解密
	 *
	 * @param key
	 * @return
	 * @throws Exception
	 */
	public static byte[] decrypt(String key) {
		try {
			return new BASE64Decoder().decodeBuffer(key);
		} catch (Exception e) {
			throw Exceptions.unchecked(e);
		}
	}

	/**
	 * BASE64加密
	 *
	 * @param key
	 * @return
	 * @throws Exception
	 */
	public static String encrypt(byte[] key) {
		return new BASE64Encoder().encodeBuffer(key);
	}
}
