package buffer;

import org.junit.Test;

import java.lang.reflect.Field;
import java.nio.ByteBuffer;

/**
 * Created by lejing on 15/11/12.
 */
public class DirectBufferTest {
	/**
	 * 显式清理
	 *
	 * @param byteBuffer
	 */
	@SuppressWarnings("restriction")
	public static void clean(final ByteBuffer byteBuffer) {
		if (byteBuffer.isDirect()) {
			((sun.nio.ch.DirectBuffer) byteBuffer).cleaner().clean();
		}
	}

	//测试人工调用DirectBuffer的清理
	@Test
	public void testDirectBufferClean() throws Exception {
		ByteBuffer buffer = ByteBuffer.allocateDirect(1024 * 1024 * 1000);
		System.out.println("start");
		Thread.sleep(5000);
		printDirectBufferInfo();
		clean(buffer);
		System.out.println("end");
		printDirectBufferInfo();
		Thread.sleep(5000);
	}

	/**
	 * 打印DirectMemory信息
	 *
	 * @throws Exception
	 */
	private void printDirectBufferInfo() throws Exception {
		Class<?> mem = Class.forName("java.nio.Bits");
		Field maxMemoryField = mem.getDeclaredField("maxMemory");
		maxMemoryField.setAccessible(true);
		Field reservedMemoryField = mem.getDeclaredField("reservedMemory");
		reservedMemoryField.setAccessible(true);
		Field totalCapacityField = mem.getDeclaredField("totalCapacity");
		totalCapacityField.setAccessible(true);
		Long maxMemory = (Long) maxMemoryField.get(null);
		Long reservedMemory = (Long) reservedMemoryField.get(null);
		Long totalCapacity = (Long) reservedMemoryField.get(null);
		System.out.println("maxMemory=" + maxMemory + ",reservedMemory=" + reservedMemory
				+ ",totalCapacity=" + totalCapacity);
	}
}
