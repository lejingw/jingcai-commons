package concurrent;

import org.apache.commons.collections.map.LRUMap;
import org.junit.Test;

public class TestLRUMap {
	@Test
	public void test1() {
		LRUMap lruMap = new LRUMap(3);
		lruMap.put("a1", "1");
		lruMap.put("a2", "2");
		lruMap.get("a2");
		lruMap.get("a2");
		lruMap.get("a2");
		lruMap.get("a1");
		lruMap.put("a3", "3");
		System.out.println(lruMap);//{a2=2, a1=1, a3=3}
		lruMap.put("a4", "4");
		System.out.println(lruMap);//{a1=1, a3=3, a4=4}
		System.out.println(lruMap.get("a2"));//null
		System.out.println(lruMap);//{a1=1, a3=3, a4=4}
		System.out.println(lruMap.get("a1"));
		System.out.println(lruMap);//{a3=3, a4=4, a1=1}
		lruMap.put("a4", "44");
		System.out.println(lruMap);//{a3=3, a1=1, a4=44}
	}
}
