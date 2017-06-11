package com.github.q9029.memcached;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class SocketPoolManager {

	private static Map<String, SocketPool> socketPoolMap = new HashMap<String, SocketPool>();

	private SocketPoolManager() {}

	public static void loadConfig() throws IOException {
		Properties props = new Properties();
		InputStream inStream = SocketPoolManager.class.getClassLoader().getResourceAsStream("memcached.properties");
		props.load(inStream);
		inStream.close();
		String pools = props.getProperty("memcached.pools");
		for (String poolName : pools.split(",")) {
			String host = props.getProperty(poolName + ".host");
			String port = props.getProperty(poolName + ".port");
			SocketPool socketPool = new SocketPool();
			socketPool.setHost(host);
			socketPool.setPort(Integer.parseInt(port));
			synchronized (socketPoolMap) {
				if (!socketPoolMap.containsKey(poolName)) {
					socketPoolMap.put(poolName, socketPool);
					socketPool.poolSockets();
				} else {
					throw new UnsupportedOperationException("same pool name is defined.");
				}
			}
		}
	}

	public static void shutdown() {
		for (SocketPool socketPool : socketPoolMap.values()) {
			if (!socketPool.isClosed()) {
				socketPool.closePooledSockets();
			}
		}
	}

	public static SocketPool getSocketPool(String poolName) {
		SocketPool socketPool;
		synchronized (socketPoolMap) {
			socketPool = socketPoolMap.get(poolName);
			if (socketPool == null) {
				socketPool = new SocketPool();
			}
		}
		return socketPool;
	}
}
