package com.github.q9029.memcached;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Map;

import com.github.q9029.memcached.SocketPool.PooledSocket;

public class MemcachedClientImpl implements MemcachedClient {

	private static final int LENGTH_CRLF = "\r\n".getBytes().length;
	private static final String SPACE = " ";
	private static final String STATS = "stats";
	private static final String GET = "get ";
	private static final String GETS = "gets ";
	private static final String SET = "set ";
	private static final String STAT = "STAT ";
	private static final String END = "END";
	private static final String VALUE = "VALUE ";
	private static final String STORED = "STORED";
	private static final String ERROR = "ERROR";

	private BufferedWriter writer;

	private BufferedReader reader;

	public MemcachedClientImpl(PooledSocket socket) throws IOException {
		this.writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"));
		this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
	}

	@Override
	public Map<String, String> stats() throws IOException {

		writer.write(STATS);
		writer.newLine();
		writer.flush();

		Map<String, String> stats = new HashMap<String, String>();
		String line;
		while ((line = reader.readLine()) != null) {
			if (line.startsWith(STAT)) {
				String[] stat = line.split(SPACE);
				stats.put(stat[1], stat[2]);
			} else if (END.equals(line)) {
				break;
			} else {
				break;
			}
		}
		return !stats.isEmpty() ? stats : null;
	}

	@Override
	public String get(String key) throws IOException {

		StringBuilder cmd = new StringBuilder();
		cmd.append(GET).append(key);
		writer.write(cmd.toString());
		writer.newLine();
		writer.flush();

		String value = null;
		String line;
		while ((line = reader.readLine()) != null) {
			if (line != null && line.startsWith(VALUE)) {
				String[] option = line.split(SPACE);
				char[] cbuf = new char[1];
				value = "";
				for (int i = 0; i < Integer.parseInt(option[3]); i++) {
					reader.read(cbuf);
					value += String.valueOf(cbuf);
				}
				reader.skip(LENGTH_CRLF);
			} else if (END.equals(line)) {
				break;
			} else {
				break;
			}
		}
		return value;
	}

	@Override
	public String[] gets(String key) throws IOException {

		StringBuilder cmd = new StringBuilder();
		cmd.append(GETS).append(key);
		writer.write(cmd.toString());
		writer.newLine();
		writer.flush();

		String[] value = null;
		String line;
		while ((line = reader.readLine()) != null) {
			if (line != null && line.startsWith(VALUE)) {
				String[] option = line.split(SPACE);
				char[] cbuf = new char[1];
				value = new String[]{option[4], ""};
				for (int i = 0; i < Integer.parseInt(option[3]); i++) {
					reader.read(cbuf);
					value[1] += String.valueOf(cbuf);
				}
				reader.skip(LENGTH_CRLF);
			} else if (END.equals(line)) {
				break;
			} else {
				break;
			}
		}
		return value;
	}

	@Override
	public boolean set(String key, String value) throws IOException {
		return set(key, "0", "0", value);
	}

	@Override
	public boolean set(String key, String flg, String seconds, String value) throws IOException {

		if (key == null) {
			throw new NullPointerException("key is null.");
		}

		String val = value != null ? value : "";

		StringBuilder cmd = new StringBuilder()
				.append(SET).append(key)
				.append(SPACE).append(flg)
				.append(SPACE).append(seconds)
				.append(SPACE).append(val.getBytes("UTF-8").length);

		writer.write(cmd.toString());
		writer.newLine();
		writer.write(val);
		writer.newLine();
		writer.flush();

		boolean result = true;
		String line;
		while ((line = reader.readLine()) != null) {
			if (STORED.equals(line)) {
				break;
			} else if (ERROR.equals(line)) {
				throw new RuntimeException("memcached set error");
			} else {
				result = false;
				break;
			}
		}
		return result;
	}

	@Override
	public boolean add(String key, String value) throws IOException {
		// TODO 自動生成されたメソッド・スタブ
		return false;
	}

	@Override
	public boolean add(String key, boolean flg, int seconds, String value) throws IOException {
		// TODO 自動生成されたメソッド・スタブ
		return false;
	}

	@Override
	public boolean replace(String key, String value) throws IOException {
		// TODO 自動生成されたメソッド・スタブ
		return false;
	}

	@Override
	public boolean replace(String key, boolean flg, int seconds, String value) throws IOException {
		// TODO 自動生成されたメソッド・スタブ
		return false;
	}

	@Override
	public boolean append(String key, String value) throws IOException {
		throw new UnsupportedOperationException("append(String, String) is not supported.");
	}

	@Override
	public boolean append(String key, boolean flg, int seconds, String value) throws IOException {
		throw new UnsupportedOperationException("append(String, boolean, int, String) is not supported.");
	}

	@Override
	public boolean prepend(String key, String value) throws IOException {
		throw new UnsupportedOperationException("prepend(String, String) is not supported.");
	}

	@Override
	public boolean prepend(String key, boolean flg, int seconds, String value) throws IOException {
		throw new UnsupportedOperationException("prepend(String, boolean, int, String) is not supported.");
	}

	@Override
	public boolean cas(String key, String casId, String value) throws IOException {
		throw new UnsupportedOperationException("cas(String, String, String) is not supported.");
	}

	@Override
	public boolean cas(String key, boolean flg, int seconds, String casId, String value) throws IOException {
		throw new UnsupportedOperationException("cas(String, String, String, String, String) is not supported.");
	}

	@Override
	public boolean delete(String key) throws IOException {
		throw new UnsupportedOperationException("delete(String, String) is not supported.");
	}

	@Override
	public int incr(String key, int value) throws IOException {
		throw new UnsupportedOperationException("incr(String, String) is not supported.");
	}

	@Override
	public int decr(String key, int value) throws IOException {
		throw new UnsupportedOperationException("decr(String, String) is not supported.");
	}
}
