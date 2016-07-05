package com.jingcai.apps.common.ice;

import Ice.Communicator;
import Ice.ObjectPrx;
import Ice.Util;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * Created by lejing on 16/7/4.
 */
public class IceGridClientUtil {
	private static volatile Communicator ic = null;
	private static Map<Class, ObjectPrx> cls2PrxMap = new HashMap<>();
	private static volatile long lastAccessTimestamp;
	private static volatile MonitorThread monitorThread;
	private static long idleTimeOutSeconds = 0;
	private static String iceLocator = null;
	private static final String locatorKey = "--Ice.Default.Locator";

	public static Communicator getIceCommunicator() {
		if (null == ic) {
			synchronized (IceGridClientUtil.class) {
				if (null == ic) {
					if (null == iceLocator) {
						ResourceBundle rb = ResourceBundle.getBundle("icegridclient", Locale.ENGLISH);
						iceLocator = rb.getString(locatorKey);
						idleTimeOutSeconds = Integer.parseInt(rb.getString("idleTimeOutSeconds"));
						System.out.println("Ice clients locator is " + iceLocator
								+ " proxy cache time out seconds:" + idleTimeOutSeconds);
					}
					String[] initParams = new String[]{locatorKey + "=" + iceLocator};
					ic = Util.initialize(initParams);
					createMonitorThread();
				}
			}
		}
		lastAccessTimestamp = System.currentTimeMillis();
		return ic;
	}

	public static void closeCommunicator(boolean removeServiceCache) {
		synchronized (IceGridClientUtil.class) {
			if (null != ic) {
				safeShutdown();
				monitorThread.interrupt();
				if (removeServiceCache && !cls2PrxMap.isEmpty()) {
					try {
						cls2PrxMap.clear();
					} catch (Exception e) {
						//ignore
					}
				}
			}
		}
	}

	private static void safeShutdown() {
		try {
			ic.shutdown();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			ic.destroy();
			ic = null;
		}
	}

	private static ObjectPrx createIceProxy(Communicator communicator, Class serviceCls) {
		String clsName = serviceCls.getName();
		String serviceName = serviceCls.getSimpleName();
		int pos = serviceName.lastIndexOf("Prx");
		if (pos < 0) {
			throw new IllegalArgumentException("Invalid ObjectPrx class, class name must end with Prx");
		}
		String realSvName = serviceName.substring(0, pos);
		ObjectPrx proxy;
		try {
			ObjectPrx base = communicator.stringToProxy(realSvName);
			proxy = (ObjectPrx) Class.forName(clsName + "Helper").newInstance();
			Method m1 = proxy.getClass().getDeclaredMethod("uncheckedCast", ObjectPrx.class);
			proxy = (ObjectPrx) m1.invoke(proxy, base);
			return proxy;
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	public static ObjectPrx getServicePrx(Class serviceCls) {
		ObjectPrx proxy = cls2PrxMap.get(serviceCls);
		if (null != proxy) {
			lastAccessTimestamp = System.currentTimeMillis();
			return proxy;
		}
		proxy = createIceProxy(getIceCommunicator(), serviceCls);
		cls2PrxMap.put(serviceCls, proxy);
		lastAccessTimestamp = System.currentTimeMillis();
		return proxy;
	}

	private static void createMonitorThread() {
		monitorThread = new MonitorThread();
		monitorThread.setDaemon(true);
		monitorThread.start();
	}

	static class MonitorThread extends Thread {
		@Override
		public void run() {
			while (!Thread.currentThread().isInterrupted()) {
				try {
					Thread.sleep(5000L);
					if (lastAccessTimestamp + idleTimeOutSeconds * 1000L < System.currentTimeMillis()) {
						closeCommunicator(true);
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
