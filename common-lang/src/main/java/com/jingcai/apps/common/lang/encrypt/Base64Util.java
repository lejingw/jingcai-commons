package com.jingcai.apps.common.lang.encrypt;

import com.jingcai.apps.common.lang.exception.Exceptions;
import org.apache.commons.codec.binary.Base64;
import sun.misc.BASE64Encoder;

import java.io.UnsupportedEncodingException;

/**
 * Created by lejing on 16/1/23.
 */
public class Base64Util {

	public static final String UTF_8 = "UTF-8";

	/**
	 * BASE64解密
	 *
	 * @param key
	 * @return
	 * @throws Exception
	 */
	public static byte[] decrypt(String key) {
		try {
			return decrypt(key.getBytes(UTF_8));
		} catch (UnsupportedEncodingException e) {
			throw Exceptions.unchecked(e);
		}
	}
	public static byte[] decrypt(byte[] key) {
			return Base64.encodeBase64(key);
	}

	/**
	 * BASE64加密
	 *
	 * @param key
	 * @return
	 * @throws Exception
	 */
	public static String encrypt(byte[] key) {
		try {
			return new String(Base64.encodeBase64(key), UTF_8);
		} catch (UnsupportedEncodingException e) {
			throw Exceptions.unchecked(e);
		}
	}
}
