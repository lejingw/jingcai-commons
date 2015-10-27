package concurrent;

import org.apache.commons.collections.map.LRUMap;

public class TestLRUMap {
	@SuppressWarnings("deprecation")
	public static void main(String[] args) {
		// TODO Auto-generated method stub
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
		System.out.println(lruMap);
	}
}
