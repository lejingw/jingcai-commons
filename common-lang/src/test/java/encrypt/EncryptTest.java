package encrypt;

import com.jingcai.apps.common.lang.encrypt.Des3Util;
import com.jingcai.apps.common.lang.encrypt.Md5;
import org.junit.Test;

/**
 * Created by lejing on 16/1/21.
 */
public class EncryptTest {
	@Test
	public void test2() {
		System.out.println("---------------------------");
		System.out.println(Des3Util.initKey());
	}
	@Test
	public void test3() {
		String key = "514345744E41596C4E41496C";
		{
			String encrypt = Des3Util.encrypt(key, "111111");
			System.out.println(encrypt);//C3EAB353AA425226
			System.out.println(Des3Util.decrypt(key, encrypt));
		}
		System.out.println("---------");
		{
			String encrypt = Des3Util.encryptBase64(key, "111111");
			System.out.println(encrypt);//w+qzU6pCUiY=\n
			System.out.println(Des3Util.decryptBase64(key, encrypt));
		}
	}
}
