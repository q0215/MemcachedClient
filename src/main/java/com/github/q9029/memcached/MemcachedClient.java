package com.github.q9029.memcached;

import java.io.IOException;
import java.util.Map;

public interface MemcachedClient {

	Map<String, String> stats() throws IOException;

	String get(String key) throws IOException;

	String[] gets(String key) throws IOException;

	boolean set(String key, String value) throws IOException;

	boolean set(String key, String flg, String seconds, String value) throws IOException;

	boolean add(String key, String value) throws IOException;

	boolean add(String key, boolean flg, int seconds, String value) throws IOException;

	boolean replace(String key, String value) throws IOException;

	boolean replace(String key, boolean flg, int seconds, String value) throws IOException;

	boolean append(String key, String value) throws IOException;

	boolean append(String key, boolean flg, int seconds, String value) throws IOException;

	boolean prepend(String key, String value) throws IOException;

	boolean prepend(String key, boolean flg, int seconds, String value) throws IOException;

	boolean cas(String key, String casId, String value) throws IOException;

	boolean cas(String key, boolean flg, int seconds, String casId, String value) throws IOException;

	boolean delete(String key) throws IOException;

	int incr(String key, int value) throws IOException;

	int decr(String key, int value) throws IOException;
}
