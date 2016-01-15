package com.jingcai.apps.common.lang.serialize;

import com.esotericsoftware.kryo.Serializer;

import java.util.ArrayList;
import java.util.List;

public class KryoUtils {
	private static final List<Class> classList = new ArrayList<Class>();
	private static final List<Serializer> serializerList = new ArrayList<Serializer>();
	private static final List<Integer> idList = new ArrayList<Integer>();

	private KryoUtils() {
	}

	private static final ThreadLocal<KryoProxy> kryos = new ThreadLocal<KryoProxy>() {
		protected KryoProxy initialValue() {
			KryoProxy kryo = new KryoProxy();
			int size = idList.size();
			for (int i = 0; i < size; i++) {
				kryo.register(classList.get(i), serializerList.get(i), idList.get(i));
			}
			kryo.setRegistrationRequired(false);//default is false
			kryo.setReferences(false);//default is true
			return kryo;
		}
	};

	/**
	 * @param className
	 * @param serializer
	 * @param id
	 */
	public static synchronized void registerClass(Class className, Serializer serializer, int id) {
		classList.add(className);
		serializerList.add(serializer);
		idList.add(id);
	}

	/**
	 * @return
	 */
	public static KryoProxy getKryo() {
		return kryos.get();
	}
}
