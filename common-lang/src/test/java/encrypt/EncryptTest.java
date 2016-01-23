package encrypt;

import com.jingcai.apps.common.lang.encrypt.Des3Util2;
import com.jingcai.apps.common.lang.encrypt.Md5;
import org.assertj.core.api.Assertions;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by lejing on 16/1/21.
 */
public class EncryptTest {
//	@Test
//	public void testDes3_old() {
//		DES3Util des3Util = DES3Util.getInstance();
//		String souece = "111111";
//		String encrypt = des3Util.encrypt(souece);
//		//C3EAB353AA425226
//		//514345744E41596C4E41496C
//		System.out.println(encrypt);
//
//		String source2 = des3Util.decrypt(encrypt);
//		Assertions.assertThat(source2).isEqualTo(souece);
//		//DES3Util
//	}
//
//
//	@Test
//	public void testDes3_new() throws Exception {
////		String key = DESCoder.initKey();
//		String inputStr = "111111";
//		String key = "514345744E41596C4E41496C";
//		System.err.println("原文:\t" + inputStr);
//		System.err.println("密钥:\t" + key);
//
//		byte[] inputData = inputStr.getBytes();
//		inputData = DESCoder.encrypt(inputData, key);
//
//		System.err.println("加密后:\t" + DESCoder.encryptBASE64(inputData));
//		System.err.println("加密后2:\t" + new String(DES3Util.toHex(inputData)));
//
//		byte[] outputData = DESCoder.decrypt(inputData, key);
//		String outputStr = new String(outputData);
//
//		System.err.println("解密后:\t" + outputStr);
//
//		Assert.assertEquals(inputStr, outputStr);
//	}

	@Test
	public void test3(){
		String key = "514345744E41596C4E41496C";
		{
			String encrypt = Des3Util2.encrypt(key, "111111");
			System.out.println(encrypt);//C3EAB353AA425226
			System.out.println(Des3Util2.decrypt(key, encrypt));
		}
		System.out.println("---------");
		{
			String encrypt = Des3Util2.encryptBase64(key, "111111");
			System.out.println(encrypt);//w+qzU6pCUiY=\n
			System.out.println(Des3Util2.decryptBase64(key, encrypt));
		}
	}

	@Test
	public void testmd5() {
		System.out.println(Md5.encode("111111"));
	}

//	@Test
//	public void testmd5_new() throws Exception {
//		String plainText = "111111";
//		byte[] bytes = Coder.encryptMD5(plainText.getBytes());
//		System.out.println(aa(bytes));
//		System.out.println(Md5.encode(plainText));
//	}

	private String aa(byte[] b) {
		int i;
		StringBuffer buf = new StringBuffer();
		for (int offset = 0; offset < b.length; offset++) {
			i = b[offset];
			if (i < 0)
				i += 256;
			if (i < 16)
				buf.append("0");
			buf.append(Integer.toHexString(i));
		}
		//32位加密
		return buf.toString();
	}
}
