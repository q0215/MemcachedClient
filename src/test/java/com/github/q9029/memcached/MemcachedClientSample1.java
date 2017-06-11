package com.github.q9029.memcached;

import java.io.IOException;

import com.github.q9029.memcached.MemcachedClient;
import com.github.q9029.memcached.MemcachedClientImpl;
import com.github.q9029.memcached.SocketPool;
import com.github.q9029.memcached.SocketPool.PooledSocket;
import com.github.q9029.memcached.SocketPoolManager;

public class MemcachedClientSample1 {

	public static void main(String[] args) throws IOException {

		// load memcached.properties and pool physical sockets
		 SocketPoolManager.loadConfig();

		// get socket pool from manager
		SocketPool socketPool = SocketPoolManager.getSocketPool("server1");

		// get logical socket from socket pool
		PooledSocket socket = socketPool.getSocket();

		// create memcached client using logical socket
		MemcachedClient client = new MemcachedClientImpl(socket);

		// business logic
		long start = System.currentTimeMillis();
		for (int i = 0; i < 10000; i++) {
			client.set("key", "value");
			client.gets("key");
		}
		System.out.println("processed " + (System.currentTimeMillis() - start) + " ms");

		// return logical socket to socket pool
		socket.close();

		// close physical socket
		socket.invalidate();

		// close physical sockets
		socketPool.closePooledSockets();

		// close socket pool manager and all pooled physical sockets
		SocketPoolManager.shutdown();
	}
}
