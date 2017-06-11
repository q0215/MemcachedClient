package com.github.q9029.memcached;

import java.io.IOException;

import com.github.q9029.memcached.MemcachedClient;
import com.github.q9029.memcached.MemcachedClientImpl;
import com.github.q9029.memcached.SocketPool;
import com.github.q9029.memcached.SocketPool.PooledSocket;

public class MemcachedClientSample3 {

	public static void main(String[] args) throws IOException {

		// create socket pool
		SocketPool socketPool = new SocketPool();

		// add pool settings on cord
		socketPool.setHost("localhost");
		socketPool.setPort(11211);

		// pool physical sockets
		socketPool.poolSockets();

		// get logical socket from socket pool
		PooledSocket socket = socketPool.getSocket();

		// create memcached client using logical socket
		MemcachedClient client = new MemcachedClientImpl(socket);

		// business logic
		long start = System.currentTimeMillis();
		for (int i = 0; i < 10000; i++) {
			client.set("key", "value");
			client.get("key");
		}
		System.out.println("processed " + (System.currentTimeMillis() - start) + "ms");

		// return logical socket to socket pool
		socket.close();

		// close physical socket
		socket.invalidate();

		// close physical sockets
		socketPool.closePooledSockets();
	}
}
