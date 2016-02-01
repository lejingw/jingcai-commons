package com.jingcai.apps.common.lang.encrypt;

import com.jingcai.apps.common.lang.exception.Exceptions;
import org.apache.commons.codec.binary.Base64;

import java.io.UnsupportedEncodingException;

/**
 * Created by lejing on 16/1/23.
 */
public class Base64Util {

	public static final String UTF_8 = "UTF-8";

	/**
	 * BASE64加密
	 */
	public static String encrypt(String key) {
		try {
			return new String(encrypt(key.getBytes(UTF_8)), UTF_8);
		} catch (UnsupportedEncodingException e) {
			throw Exceptions.unchecked(e);
		}
	}

	/**
	 * BASE64加密
	 */
	public static byte[] encrypt(byte[] key) {
		return Base64.encodeBase64(key);
	}

	/**
	 * BASE64解密
	 */
	public static String decrypt(String key) {
		try {
			return new String(decrypt(key.getBytes(UTF_8)), UTF_8);
		} catch (UnsupportedEncodingException e) {
			throw Exceptions.unchecked(e);
		}
	}

	/**
	 * BASE64解密
	 */
	public static byte[] decrypt(byte[] key) {
		return Base64.decodeBase64(key);
	}
}
