package com.github.q9029.memcached;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class SocketPool {

	private Queue<PooledSocket> socketPool = new ConcurrentLinkedQueue<PooledSocket>();

	private boolean closed = true;

	private String host;

	private int port;

	private int maxPoolSize = 10;

	private int minPoolSize = 10;

	private int maxWaitTime = 30000;

	public void setHost(String host) {
		this.host = host;
	}

	public String getHost() {
		return host;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public int getPort() {
		return port;
	}

	public int getMaxPoolSize() {
		return maxPoolSize;
	}

	public void setMaxPoolSize(int maxPoolSize) {
		this.maxPoolSize = maxPoolSize;
	}

	public int getMinPoolSize() {
		return minPoolSize;
	}

	public void setMinPoolSize(int minPoolSize) {
		this.minPoolSize = minPoolSize;
	}

	public boolean isClosed() {
		return closed;
	}

	// fix
	public void poolSockets() throws UnknownHostException, IOException {
		closed = false;
		try {
			for (int i = 0; i < minPoolSize; i++) {
				socketPool.offer(new PooledSocket());
			}
		} catch (UnknownHostException e) {
			closePooledSockets();
			throw e;

		} catch (IOException e) {
			closePooledSockets();
			throw e;
		}
	}

	// fix
	public void closePooledSockets() {
		if (!closed) {
			for (PooledSocket socket : socketPool) {
				try {
					socket.getSocket().close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				socketPool.remove(socket);
			}
			closed = true;
		} else {
			throw new UnsupportedOperationException("This pool is already closed.");
		}
	}

	// TODO
	public synchronized PooledSocket getSocket() {
		PooledSocket socket;
		long start = System.currentTimeMillis();
		while (true) {
			socket = socketPool.poll();
			if (socket != null) {
				break;
			}
			if (maxWaitTime < System.currentTimeMillis() - start) {
				throw new RuntimeException("To get socket from socket pool is timeout.");
			}
		}
		return socket;
	}

	// fix
	private void closeSocket(PooledSocket socket) throws IOException {
		if (!closed) {
			socketPool.offer(socket);
		} else {
			invalidateSocket(socket);
		}
	}

	// fix
	private void invalidateSocket(PooledSocket socket) throws IOException {
		socket.getSocket().close();
		socketPool.remove(socket);
	}

	public class PooledSocket implements Closeable {

		private Socket socket;

		// TODO
		private PooledSocket() throws UnknownHostException, IOException {
			socket = new Socket(SocketPool.this.host, SocketPool.this.port);
			// その他設定の追加
		}

		// fix
		private Socket getSocket() {
			return socket;
		}

		public InputStream getInputStream() throws IOException {
			return socket.getInputStream();
		}

		public OutputStream getOutputStream() throws IOException {
			return socket.getOutputStream();
		}

		// fix
		@Override
		public void close() throws IOException {
			SocketPool.this.closeSocket(this);
		}

		// fix
		public void invalidate() throws IOException {
			SocketPool.this.invalidateSocket(this);
		}
	}
}
