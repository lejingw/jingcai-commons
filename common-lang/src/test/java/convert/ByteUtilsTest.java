package convert;

import com.jingcai.apps.common.lang.convert.ByteUtils;
import org.assertj.core.api.Assertions;
import org.junit.Test;

/**
 * Created by lejing on 16/11/24.
 */
public class ByteUtilsTest {
	@Test
	public void test1() {
		int raw = 1234567890;
		int len = 8;
		byte[] bytes = ByteUtils.int2Bytes(raw, len);

		int result = ByteUtils.bytes2Int(bytes, 0, len);
		System.out.println("raw = " + raw + "\t\tresult = " + result);
		Assertions.assertThat(result).isEqualTo(raw);
	}
	@Test
	public void test2() {
		int raw = 1234567890;
		byte[] bytes = int2Bytes(raw);

		int result = bytes2Int(bytes);
		System.out.println("raw = " + raw + "\t\tresult = " + result);
		Assertions.assertThat(result).isEqualTo(raw);
	}

	private int bytes2Int(byte[] b) {
		int sum = 0;
		for (int i = 0; i < 4; i++) {
			int n = ((int) b[i]) & 0xff;
			n <<= (3 - i) * 8;
			sum = n + sum;
		}
		return sum;
	}

	private byte[] int2Bytes(int value) {
		byte[] b = new byte[4];
		for (int i = 0; i < 4; i++) {
			b[3 - i] = (byte) ((value >> 8 * i) & 0xff);
		}
		return b;
	}
}
