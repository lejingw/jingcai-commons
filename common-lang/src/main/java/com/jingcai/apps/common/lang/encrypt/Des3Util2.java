package com.jingcai.apps.common.lang.encrypt;

import com.jingcai.apps.common.lang.exception.Exceptions;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.Key;
import java.security.SecureRandom;

/**
 * Created by lejing on 16/1/23.
 */
public class Des3Util2 {
	private static final String charsetName = "UTF-8";
	private static final String ALGORITHM = "DES";


	/**
	 * str 默认取16进制编码
	 *
	 * @param str
	 * @return
	 */
	public static String encrypt(String key, String str) {
		byte[] bytes = encrypt(toByte(key), toByte(str));
		return new String(HexUtil.toHex(bytes));
	}

	/**
	 * str 默认取16进制解码
	 *
	 * @param str
	 * @return
	 */
	public static String decrypt(String key, String str) {
		byte[] bytes = decrypt(toByte(key), HexUtil.fromHex(toByte(str)));
		return new String(bytes);
	}

	/**
	 * str Base64编码
	 *
	 * @param str
	 * @return
	 */
	public static String encryptBase64(String key, String str) {
		byte[] bytes = encrypt(toByte(key), toByte(str));
		return Base64Util.encrypt(bytes);
	}

	/**
	 * str Base64解码
	 *
	 * @param str
	 * @return
	 */
	public static String decryptBase64(String key, String str) {
		byte[] bytes = decrypt(toByte(key), Base64Util.decrypt(str));
		return new String(bytes);
	}


	public static byte[] encrypt(byte[] keyBytes, byte[] data) {
		Key key = getKey(keyBytes);
		try {
			Cipher cipher = Cipher.getInstance(ALGORITHM);
			cipher.init(Cipher.ENCRYPT_MODE, key);
			return cipher.doFinal(data);
		} catch (Exception e) {
			throw Exceptions.unchecked(e);
		}
	}

	public static byte[] decrypt(byte[] keyBytes, byte[] data) {
		Key key = getKey(keyBytes);
		try {
			Cipher cipher = Cipher.getInstance(ALGORITHM);
			cipher.init(Cipher.DECRYPT_MODE, key);

			return cipher.doFinal(data);
		} catch (Exception e) {
			throw Exceptions.unchecked(e);
		}
	}


	/**
	 * 转换密钥<br>
	 *
	 * @param key
	 * @return
	 * @throws Exception
	 */
	private static Key getKey(byte[] key) {
		try {
			DESKeySpec dks = new DESKeySpec(key);
			SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(ALGORITHM);
			SecretKey secretKey = keyFactory.generateSecret(dks);

			// 当使用其他对称加密算法时，如AES、Blowfish等算法时，用下述代码替换上述三行代码
			// SecretKey secretKey = new SecretKeySpec(key, ALGORITHM);

			return secretKey;
		} catch (Exception e) {
			throw Exceptions.unchecked(e);
		}
	}

	private static byte[] toByte(String str) {
		try {
			return str.getBytes(charsetName);
		} catch (UnsupportedEncodingException e) {
			throw Exceptions.unchecked(e);
		}
	}

	/**
	 * 生成密钥
	 *
	 * @return
	 * @throws Exception
	 */
	public static String initKey() throws Exception {
		return initKey(null);
	}

	/**
	 * 生成密钥
	 *
	 * @param seed
	 * @return
	 * @throws Exception
	 */
	public static String initKey(String seed) throws Exception {
		SecureRandom secureRandom = null;

		if (seed != null) {
			secureRandom = new SecureRandom(Base64Util.decrypt(seed));
		} else {
			secureRandom = new SecureRandom();
		}

		KeyGenerator kg = KeyGenerator.getInstance(ALGORITHM);
		kg.init(secureRandom);

		SecretKey secretKey = kg.generateKey();

		return Base64Util.encrypt(secretKey.getEncoded());
	}
}
