package com.jingcai.apps.common.ice;

import Ice.*;
import Ice.Object;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by lejing on 16/7/4.
 */
@Slf4j
public class PerfDispatchInterceptor extends DispatchInterceptor {
	private static final Map<Identity, Ice.Object> ice2ObjMap = new ConcurrentHashMap<>();
	private static final PerfDispatchInterceptor inst = new PerfDispatchInterceptor();

	private PerfDispatchInterceptor() {
	}

	public static PerfDispatchInterceptor getInstance() {
		return inst;
	}

	public static DispatchInterceptor addIceObject(Identity id, Object iceObj) {
		log.info("add ice object " + id);
		ice2ObjMap.put(id, iceObj);
		return inst;
	}

	public static void removeIceObject(Identity id) {
		log.info("remove ice object " + id);
		ice2ObjMap.remove(id);
	}

	@Override
	public DispatchStatus dispatch(Request request) {
		Identity id = request.getCurrent().id;
		Connection con = request.getCurrent().con;
		String inf = "dispatch request, method:" + request.getCurrent().operation
				+ " service:" + id.name + " " + con;
		log.info("begin " + inf);
		try {
			DispatchStatus result = ice2ObjMap.get(id).ice_dispatch(request);
			log.debug("end " + inf);
			return result;
		} catch (RuntimeException e) {
			log.debug("error " + inf, e);
			throw e;
		}
	}
}
