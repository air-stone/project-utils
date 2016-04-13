/**
 * 
 */
package com.air.project.utils.redis;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.air.project.utils.property.PropertiesLoader;
import com.air.project.utils.string.StringUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisSentinelPool;
import redis.clients.jedis.exceptions.JedisConnectionException;
import redis.clients.jedis.exceptions.JedisException;

/**
 * @author air
 *
 *         2016年1月26日 下午5:54:55
 */
public class JedisSentinelUtils {
	private static final Logger LOG = LoggerFactory.getLogger(JedisSentinelUtils.class);

	private static JedisSentinelPool sentinelPool;
	/**
	 * 属性文件加载对象
	 */
	private static PropertiesLoader loader = new PropertiesLoader("/jedis/jedis.properties");

	private static Set<String> sentinels = new HashSet<String>();

	static {
		int size = loader.getInteger("sentinels.size");
		for (int i = 1; i <= size; i++) {
			String ip = loader.getProperty("sentinels" + i + ".ip");
			int port = loader.getInteger("sentinels" + i + ".port");
			sentinels.add(new HostAndPort(ip, port).toString());
		}
		initPool();
	}
	private JedisSentinelUtils(){
		
	}
	private static void initPool() {
		sentinelPool = new JedisSentinelPool(loader.getProperty("sentinels1.masterName"), sentinels);
		LOG.info("Current master:{0} ", sentinelPool.getCurrentHostMaster().toString());
	}

	/**
	 * 获取缓存
	 * 
	 * @param key
	 *            键
	 * @return 值
	 */
	public static String get(String key) {
		String value = null;
		Jedis jedis = null;
		try {
			jedis = getResource();
			if (jedis.exists(key)) {
				value = jedis.get(key);
				value = StringUtils.isNotBlank(value) && !"nil".equalsIgnoreCase(value) ? value : null;
				LOG.debug("get {} = {}", key, value);
			}
		} catch (JedisConnectionException e) {
			returnBrokenResource(jedis);
			jedis = null;
		} catch (Exception e) {
			LOG.warn("get {} = {}", key, value, e);
		} finally {
			returnResource(jedis);
		}
		return value;
	}

	/**
	 * 获取缓存
	 * 
	 * @param key
	 *            键
	 * @return 值
	 */
	public static Object getObject(String key) {
		Object value = null;
		Jedis jedis = null;
		try {
			jedis = getResource();
			if (jedis.exists(getBytesKey(key))) {
				value = toObject(jedis.get(getBytesKey(key)));
				LOG.debug("getObject {} = {}", key, value);
			}
		} catch (JedisConnectionException e) {
			returnBrokenResource(jedis);
			jedis = null;
		} catch (Exception e) {
			LOG.warn("getObject {} = {}", key, value, e);
		} finally {
			returnResource(jedis);
		}
		return value;
	}

	/**
	 * 设置缓存
	 * 
	 * @param key
	 *            键
	 * @param value
	 *            值
	 * @param cacheSeconds
	 *            超时时间，0为不超时
	 * @return
	 */
	public static String set(String key, String value, int cacheSeconds) {
		String result = null;
		Jedis jedis = null;
		try {
			jedis = getResource();
			result = jedis.set(key, value);
			if (cacheSeconds != 0) {
				jedis.expire(key, cacheSeconds);
			}
			LOG.debug("set {} = {}", key, value);

		} catch (JedisConnectionException e) {
			returnBrokenResource(jedis);
			jedis = null;
		} catch (Exception e) {
			LOG.warn("set {} = {}", key, value, e);
		} finally {
			returnResource(jedis);
		}
		return result;
	}

	/**
	 * 设置缓存
	 * 
	 * @param key
	 *            键
	 * @param value
	 *            值
	 * @param cacheSeconds
	 *            超时时间，0为不超时
	 * @return
	 */
	public static String setObject(String key, Object value, int cacheSeconds) {
		String result = null;
		Jedis jedis = null;
		try {
			jedis = getResource();
			result = jedis.set(getBytesKey(key), toBytes(value));
			if (cacheSeconds != 0) {
				jedis.expire(key, cacheSeconds);
			}
			LOG.debug("setObject {} = {}", key, value);
		} catch (JedisConnectionException e) {
			returnBrokenResource(jedis);
			jedis = null;
		} catch (Exception e) {
			LOG.warn("setObject {} = {}", key, value, e);
		} finally {
			returnResource(jedis);
		}
		return result;
	}

	/**
	 * 获取List缓存
	 * 
	 * @param key
	 *            键
	 * @return 值
	 */
	public static List<String> getList(String key) {
		List<String> value = null;
		Jedis jedis = null;
		try {
			jedis = getResource();
			if (jedis.exists(key)) {
				value = jedis.lrange(key, 0, -1);
				LOG.debug("getList {} = {}", key, value);
			}
		} catch (JedisConnectionException e) {
			returnBrokenResource(jedis);
			jedis = null;
		} catch (Exception e) {
			LOG.warn("getList {} = {}", key, value, e);
		} finally {
			returnResource(jedis);
		}
		return value;
	}

	/**
	 * 获取List缓存
	 * 
	 * @param key
	 *            键
	 * @return 值
	 */
	public static List<Object> getObjectList(String key) {
		List<Object> value = null;
		Jedis jedis = null;
		try {
			jedis = getResource();
			if (jedis.exists(getBytesKey(key))) {
				List<byte[]> list = jedis.lrange(getBytesKey(key), 0, -1);
				value = Lists.newArrayList();
				for (byte[] bs : list) {
					value.add(toObject(bs));
				}
				LOG.debug("getObjectList {} = {}", key, value);
			}
		} catch (JedisConnectionException e) {
			returnBrokenResource(jedis);
			jedis = null;
		} catch (Exception e) {
			LOG.warn("getObjectList {} = {}", key, value, e);
		} finally {
			returnResource(jedis);
		}
		return value;
	}

	/**
	 * 设置List缓存
	 * 
	 * @param key
	 *            键
	 * @param value
	 *            值
	 * @param cacheSeconds
	 *            超时时间，0为不超时
	 * @return
	 */
	public static long setList(String key, List<String> value, int cacheSeconds) {
		long result = 0;
		Jedis jedis = null;
		try {
			jedis = getResource();
			if (jedis.exists(key)) {
				jedis.del(key);
			}
			result = jedis.rpush(key, (String[]) value.toArray());
			if (cacheSeconds != 0) {
				jedis.expire(key, cacheSeconds);
			}
			LOG.debug("setList {} = {}", key, value);
		} catch (JedisConnectionException e) {
			returnBrokenResource(jedis);
			jedis = null;
		} catch (Exception e) {
			LOG.warn("setList {} = {}", key, value, e);
		} finally {
			returnResource(jedis);
		}
		return result;
	}

	/**
	 * 设置List缓存
	 * 
	 * @param key
	 *            键
	 * @param value
	 *            值
	 * @param cacheSeconds
	 *            超时时间，0为不超时
	 * @return
	 */
	public static long setObjectList(String key, List<Object> value, int cacheSeconds) {
		long result = 0;
		Jedis jedis = null;
		try {
			jedis = getResource();
			if (jedis.exists(getBytesKey(key))) {
				jedis.del(key);
			}
			List<byte[]> list = Lists.newArrayList();
			for (Object o : value) {
				list.add(toBytes(o));
			}
			result = jedis.rpush(getBytesKey(key), (byte[][]) list.toArray());
			if (cacheSeconds != 0) {
				jedis.expire(key, cacheSeconds);
			}
			LOG.debug("setObjectList {} = {}", key, value);
		} catch (JedisConnectionException e) {
			returnBrokenResource(jedis);
			jedis = null;
		} catch (Exception e) {
			LOG.warn("setObjectList {} = {}", key, value, e);
		} finally {
			returnResource(jedis);
		}
		return result;
	}

	/**
	 * 向List缓存中添加值
	 * 
	 * @param key
	 *            键
	 * @param value
	 *            值
	 * @return
	 */
	public static long listAdd(String key, String... value) {
		long result = 0;
		Jedis jedis = null;
		try {
			jedis = getResource();
			result = jedis.rpush(key, value);
			LOG.debug("listAdd {} = {}", key, value);
		} catch (JedisConnectionException e) {
			returnBrokenResource(jedis);
			jedis = null;
		} catch (Exception e) {
			LOG.warn("listAdd {} = {}", key, value, e);
		} finally {
			returnResource(jedis);
		}
		return result;
	}

	/**
	 * 向List缓存中添加值
	 * 
	 * @param key
	 *            键
	 * @param value
	 *            值
	 * @return
	 */
	public static long listObjectAdd(String key, Object... value) {
		long result = 0;
		Jedis jedis = null;
		try {
			jedis = getResource();
			List<byte[]> list = Lists.newArrayList();
			for (Object o : value) {
				list.add(toBytes(o));
			}
			result = jedis.rpush(getBytesKey(key), (byte[][]) list.toArray());
			LOG.debug("listObjectAdd {} = {}", key, value);
		} catch (JedisConnectionException e) {
			returnBrokenResource(jedis);
			jedis = null;
		} catch (Exception e) {
			LOG.warn("listObjectAdd {} = {}", key, value, e);
		} finally {
			returnResource(jedis);
		}
		return result;
	}

	/**
	 * 获取缓存
	 * 
	 * @param key
	 *            键
	 * @return 值
	 */
	public static Set<String> getSet(String key) {
		Set<String> value = null;
		Jedis jedis = null;
		try {
			jedis = getResource();
			if (jedis.exists(key)) {
				value = jedis.smembers(key);
				LOG.debug("getSet {} = {}", key, value);
			}
		} catch (JedisConnectionException e) {
			returnBrokenResource(jedis);
			jedis = null;
		} catch (Exception e) {
			LOG.warn("getSet {} = {}", key, value, e);
		} finally {
			returnResource(jedis);
		}
		return value;
	}

	/**
	 * 获取缓存
	 * 
	 * @param key
	 *            键
	 * @return 值
	 */
	public static Set<Object> getObjectSet(String key) {
		Set<Object> value = null;
		Jedis jedis = null;
		try {
			jedis = getResource();
			if (jedis.exists(getBytesKey(key))) {
				value = Sets.newHashSet();
				Set<byte[]> set = jedis.smembers(getBytesKey(key));
				for (byte[] bs : set) {
					value.add(toObject(bs));
				}
				LOG.debug("getObjectSet {} = {}", key, value);
			}
		} catch (JedisConnectionException e) {
			returnBrokenResource(jedis);
			jedis = null;
		} catch (Exception e) {
			LOG.warn("getObjectSet {} = {}", key, value, e);
		} finally {
			returnResource(jedis);
		}
		return value;
	}

	/**
	 * 设置Set缓存
	 * 
	 * @param key
	 *            键
	 * @param value
	 *            值
	 * @param cacheSeconds
	 *            超时时间，0为不超时
	 * @return
	 */
	public static long setSet(String key, Set<String> value, int cacheSeconds) {
		long result = 0;
		Jedis jedis = null;
		try {
			jedis = getResource();
			if (jedis.exists(key)) {
				jedis.del(key);
			}
			result = jedis.sadd(key, (String[]) value.toArray());
			if (cacheSeconds != 0) {
				jedis.expire(key, cacheSeconds);
			}
			LOG.debug("setSet {} = {}", key, value);
		} catch (JedisConnectionException e) {
			returnBrokenResource(jedis);
			jedis = null;
		} catch (Exception e) {
			LOG.warn("setSet {} = {}", key, value, e);
		} finally {
			returnResource(jedis);
		}
		return result;
	}

	/**
	 * 设置Set缓存
	 * 
	 * @param key
	 *            键
	 * @param value
	 *            值
	 * @param cacheSeconds
	 *            超时时间，0为不超时
	 * @return
	 */
	public static long setObjectSet(String key, Set<Object> value, int cacheSeconds) {
		long result = 0;
		Jedis jedis = null;
		try {
			jedis = getResource();
			if (jedis.exists(getBytesKey(key))) {
				jedis.del(key);
			}
			Set<byte[]> set = Sets.newHashSet();
			for (Object o : value) {
				set.add(toBytes(o));
			}
			result = jedis.sadd(getBytesKey(key), (byte[][]) set.toArray());
			if (cacheSeconds != 0) {
				jedis.expire(key, cacheSeconds);
			}
			LOG.debug("setObjectSet {} = {}", key, value);
		} catch (JedisConnectionException e) {
			returnBrokenResource(jedis);
			jedis = null;
		} catch (Exception e) {
			LOG.warn("setObjectSet {} = {}", key, value, e);
		} finally {
			returnResource(jedis);
		}
		return result;
	}

	/**
	 * 向Set缓存中添加值
	 * 
	 * @param key
	 *            键
	 * @param value
	 *            值
	 * @return
	 */
	public static long setSetAdd(String key, String... value) {
		long result = 0;
		Jedis jedis = null;
		try {
			jedis = getResource();
			result = jedis.sadd(key, value);
			LOG.debug("setSetAdd {} = {}", key, value);
		} catch (JedisConnectionException e) {
			returnBrokenResource(jedis);
			jedis = null;
		} catch (Exception e) {
			LOG.warn("setSetAdd {} = {}", key, value, e);
		} finally {
			returnResource(jedis);
		}
		return result;
	}

	/**
	 * 向Set缓存中添加值
	 * 
	 * @param key
	 *            键
	 * @param value
	 *            值
	 * @return
	 */
	public static long setSetObjectAdd(String key, Object... value) {
		long result = 0;
		Jedis jedis = null;
		try {
			jedis = getResource();
			Set<byte[]> set = Sets.newHashSet();
			for (Object o : value) {
				set.add(toBytes(o));
			}
			result = jedis.rpush(getBytesKey(key), (byte[][]) set.toArray());
			LOG.debug("setSetObjectAdd {} = {}", key, value);
		} catch (JedisConnectionException e) {
			returnBrokenResource(jedis);
			jedis = null;
		} catch (Exception e) {
			LOG.warn("setSetObjectAdd {} = {}", key, value, e);
		} finally {
			returnResource(jedis);
		}
		return result;
	}

	/**
	 * 获取Map缓存
	 * 
	 * @param key
	 *            键
	 * @return 值
	 */
	public static Map<String, String> getMap(String key) {
		Map<String, String> value = null;
		Jedis jedis = null;
		try {
			jedis = getResource();
			if (jedis.exists(key)) {
				value = jedis.hgetAll(key);
				LOG.debug("getMap {} = {}", key, value);
			}
		} catch (JedisConnectionException e) {
			returnBrokenResource(jedis);
			jedis = null;
		} catch (Exception e) {
			LOG.warn("getMap {} = {}", key, value, e);
		} finally {
			returnResource(jedis);
		}
		return value;
	}

	/**
	 * 获取Map缓存
	 * 
	 * @param key
	 *            键
	 * @return 值
	 */
	public static Map<String, Object> getObjectMap(String key) {
		Map<String, Object> value = null;
		Jedis jedis = null;
		try {
			jedis = getResource();
			if (jedis.exists(getBytesKey(key))) {
				value = Maps.newHashMap();
				Map<byte[], byte[]> map = jedis.hgetAll(getBytesKey(key));
				for (Map.Entry<byte[], byte[]> e : map.entrySet()) {
					value.put(StringUtils.toString(e.getKey()), toObject(e.getValue()));
				}
				LOG.debug("getObjectMap {} = {}", key, value);
			}
		} catch (JedisConnectionException e) {
			returnBrokenResource(jedis);
			jedis = null;
		} catch (Exception e) {
			LOG.warn("getObjectMap {} = {}", key, value, e);
		} finally {
			returnResource(jedis);
		}
		return value;
	}

	/**
	 * 设置Map缓存
	 * 
	 * @param key
	 *            键
	 * @param value
	 *            值
	 * @param cacheSeconds
	 *            超时时间，0为不超时
	 * @return
	 */
	public static String setMap(String key, Map<String, String> value, int cacheSeconds) {
		String result = null;
		Jedis jedis = null;
		try {
			jedis = getResource();
			if (jedis.exists(key)) {
				jedis.del(key);
			}
			result = jedis.hmset(key, value);
			if (cacheSeconds != 0) {
				jedis.expire(key, cacheSeconds);
			}
			LOG.debug("setMap {} = {}", key, value);
		} catch (JedisConnectionException e) {
			returnBrokenResource(jedis);
			jedis = null;
		} catch (Exception e) {
			LOG.warn("setMap {} = {}", key, value, e);
		} finally {
			returnResource(jedis);
		}
		return result;
	}

	/**
	 * 设置Map缓存
	 * 
	 * @param key
	 *            键
	 * @param value
	 *            值
	 * @param cacheSeconds
	 *            超时时间，0为不超时
	 * @return
	 */
	public static String setObjectMap(String key, Map<String, Object> value, int cacheSeconds) {
		String result = null;
		Jedis jedis = null;
		try {
			jedis = getResource();
			if (jedis.exists(getBytesKey(key))) {
				jedis.del(key);
			}
			Map<byte[], byte[]> map = Maps.newHashMap();
			for (Map.Entry<String, Object> e : value.entrySet()) {
				map.put(getBytesKey(e.getKey()), toBytes(e.getValue()));
			}
			result = jedis.hmset(getBytesKey(key), (Map<byte[], byte[]>) map);
			if (cacheSeconds != 0) {
				jedis.expire(key, cacheSeconds);
			}
			LOG.debug("setObjectMap {} = {}", key, value);
		} catch (JedisConnectionException e) {
			returnBrokenResource(jedis);
			jedis = null;
		} catch (Exception e) {
			LOG.warn("setObjectMap {} = {}", key, value, e);
		} finally {
			returnResource(jedis);
		}
		return result;
	}

	/**
	 * 向Map缓存中添加值
	 * 
	 * @param key
	 *            键
	 * @param value
	 *            值
	 * @return
	 */
	public static String mapPut(String key, Map<String, String> value) {
		String result = null;
		Jedis jedis = null;
		try {
			jedis = getResource();
			result = jedis.hmset(key, value);
			LOG.debug("mapPut {} = {}", key, value);
		} catch (JedisConnectionException e) {
			returnBrokenResource(jedis);
			jedis = null;
		} catch (Exception e) {
			LOG.warn("mapPut {} = {}", key, value, e);
		} finally {
			returnResource(jedis);
		}
		return result;
	}

	/**
	 * 向Map缓存中添加值
	 * 
	 * @param key
	 *            键
	 * @param value
	 *            值
	 * @return
	 */
	public static String mapObjectPut(String key, Map<String, Object> value) {
		String result = null;
		Jedis jedis = null;
		try {
			jedis = getResource();
			Map<byte[], byte[]> map = Maps.newHashMap();
			for (Map.Entry<String, Object> e : value.entrySet()) {
				map.put(getBytesKey(e.getKey()), toBytes(e.getValue()));
			}
			result = jedis.hmset(getBytesKey(key), (Map<byte[], byte[]>) map);
			LOG.debug("mapObjectPut {} = {}", key, value);
		} catch (JedisConnectionException e) {
			returnBrokenResource(jedis);
			jedis = null;
		} catch (Exception e) {
			LOG.warn("mapObjectPut {} = {}", key, value, e);
		} finally {
			returnResource(jedis);
		}
		return result;
	}

	/**
	 * 移除Map缓存中的值
	 * 
	 * @param key
	 *            键
	 * @param value
	 *            值
	 * @return
	 */
	public static long mapRemove(String key, String mapKey) {
		long result = 0;
		Jedis jedis = null;
		try {
			jedis = getResource();
			result = jedis.hdel(key, mapKey);
			LOG.debug("mapRemove {}  {}", key, mapKey);
		} catch (JedisConnectionException e) {
			returnBrokenResource(jedis);
			jedis = null;
		} catch (Exception e) {
			LOG.warn("mapRemove {}  {}", key, mapKey, e);
		} finally {
			returnResource(jedis);
		}
		return result;
	}

	/**
	 * 移除Map缓存中的值
	 * 
	 * @param key
	 *            键
	 * @param value
	 *            值
	 * @return
	 */
	public static long mapObjectRemove(String key, String mapKey) {
		long result = 0;
		Jedis jedis = null;
		try {
			jedis = getResource();
			result = jedis.hdel(getBytesKey(key), getBytesKey(mapKey));
			LOG.debug("mapObjectRemove {}  {}", key, mapKey);
		} catch (JedisConnectionException e) {
			returnBrokenResource(jedis);
			jedis = null;
		} catch (Exception e) {
			LOG.warn("mapObjectRemove {}  {}", key, mapKey, e);
		} finally {
			returnResource(jedis);
		}
		return result;
	}

	/**
	 * 判断Map缓存中的Key是否存在
	 * 
	 * @param key
	 *            键
	 * @param value
	 *            值
	 * @return
	 */
	public static boolean mapExists(String key, String mapKey) {
		boolean result = false;
		Jedis jedis = null;
		try {
			jedis = getResource();
			result = jedis.hexists(key, mapKey);
			LOG.debug("mapExists {}  {}", key, mapKey);
		} catch (JedisConnectionException e) {
			returnBrokenResource(jedis);
			jedis = null;
		} catch (Exception e) {
			LOG.warn("mapExists {}  {}", key, mapKey, e);
		} finally {
			returnResource(jedis);
		}
		return result;
	}

	/**
	 * 判断Map缓存中的Key是否存在
	 * 
	 * @param key
	 *            键
	 * @param value
	 *            值
	 * @return
	 */
	public static boolean mapObjectExists(String key, String mapKey) {
		boolean result = false;
		Jedis jedis = null;
		try {
			jedis = getResource();
			result = jedis.hexists(getBytesKey(key), getBytesKey(mapKey));
			LOG.debug("mapObjectExists {}  {}", key, mapKey);
		} catch (JedisConnectionException e) {
			returnBrokenResource(jedis);
			jedis = null;
		} catch (Exception e) {
			LOG.warn("mapObjectExists {}  {}", key, mapKey, e);
		} finally {
			returnResource(jedis);
		}
		return result;
	}

	/**
	 * 删除缓存
	 * 
	 * @param key
	 *            键
	 * @return
	 */
	public static long del(String key) {
		long result = 0;
		Jedis jedis = null;
		try {
			jedis = getResource();
			if (jedis.exists(key)) {
				result = jedis.del(key);
				LOG.debug("del {}", key);
			} else {
				LOG.debug("del {} not exists", key);
			}
		} catch (JedisConnectionException e) {
			returnBrokenResource(jedis);
			jedis = null;
		} catch (Exception e) {
			LOG.warn("del {}", key, e);
		} finally {
			returnResource(jedis);
		}
		return result;
	}

	/**
	 * 删除缓存
	 * 
	 * @param key
	 *            键
	 * @return
	 */
	public static long delObject(String key) {
		long result = 0;
		Jedis jedis = null;
		try {
			jedis = getResource();
			if (jedis.exists(getBytesKey(key))) {
				result = jedis.del(getBytesKey(key));
				LOG.debug("delObject {}", key);
			} else {
				LOG.debug("delObject {} not exists", key);
			}
		} catch (JedisConnectionException e) {
			returnBrokenResource(jedis);
			jedis = null;
		} catch (Exception e) {
			LOG.warn("delObject {}", key, e);
		} finally {
			returnResource(jedis);
		}
		return result;
	}

	/**
	 * 设置过期时间
	 * 
	 * @param key
	 *            键
	 * @return
	 */
	public static long expire(String key, int seconds) {
		long result = 0;
		Jedis jedis = null;
		try {
			jedis = getResource();
			if (jedis.exists(getBytesKey(key))) {
				result = jedis.expire(getBytesKey(key), seconds);
				LOG.debug("expire {}", key);
			} else {
				LOG.debug("expire {} not exists", key);
			}
		} catch (JedisConnectionException e) {
			returnBrokenResource(jedis);
			jedis = null;
		} catch (Exception e) {
			LOG.warn("expire {}", key, e);
		} finally {
			returnResource(jedis);
		}
		return result;
	}

	/**
	 * 缓存是否存在
	 * 
	 * @param key
	 *            键
	 * @return
	 */
	public static boolean exists(String key) {
		boolean result = false;
		Jedis jedis = null;
		try {
			jedis = getResource();
			result = jedis.exists(key);
			LOG.debug("exists {}", key);
		} catch (JedisConnectionException e) {
			returnBrokenResource(jedis);
			jedis = null;
		} catch (Exception e) {
			LOG.warn("exists {}", key, e);
		} finally {
			returnResource(jedis);
		}
		return result;
	}

	/**
	 * 缓存是否存在
	 * 
	 * @param key
	 *            键
	 * @return
	 */
	public static boolean existsObject(String key) {
		boolean result = false;
		Jedis jedis = null;
		try {
			jedis = getResource();
			result = jedis.exists(getBytesKey(key));
			LOG.debug("existsObject {}", key);
		} catch (JedisConnectionException e) {
			returnBrokenResource(jedis);
			jedis = null;
		} catch (Exception e) {
			LOG.warn("existsObject {}", key, e);
		} finally {
			returnResource(jedis);
		}
		return result;
	}

	/**
	 * 获取资源
	 * 
	 * @return
	 * @throws JedisException
	 */
	public static Jedis getResource() throws JedisException {
		Jedis jedis = null;
		try {
			jedis = sentinelPool.getResource();
		} catch (JedisException e) {
			LOG.warn("getResource.", e);
			returnBrokenResource(jedis);
			throw e;
		}
		return jedis;
	}

	/**
	 * 归还资源
	 * 
	 * @param jedis
	 * @param isBroken
	 */
	public static void returnBrokenResource(Jedis jedis) {
		if (jedis != null) {
			sentinelPool.returnBrokenResource(jedis);
		}
	}

	/**
	 * 释放资源
	 * 
	 * @param jedis
	 * @param isBroken
	 */
	public static void returnResource(Jedis jedis) {
		if (jedis != null) {
			sentinelPool.returnResource(jedis);
		}
	}

	/**
	 * 获取byte[]类型Key
	 * 
	 * @param key
	 * @return
	 */
	public static byte[] getBytesKey(Object object) {
		if (object instanceof String) {
			return StringUtils.getBytes((String) object);
		} else {
			return ObjectUtils.serialize(object);
		}
	}

	/**
	 * Object转换byte[]类型
	 * 
	 * @param key
	 * @return
	 */
	public static byte[] toBytes(Object object) {
		return ObjectUtils.serialize(object);
	}

	/**
	 * byte[]型转换Object
	 * 
	 * @param key
	 * @return
	 */
	public static Object toObject(byte[] bytes) {
		return ObjectUtils.unserialize(bytes);
	}

	/**
	 * 查看匹配所有keys
	 * 
	 * @param pattern
	 * @return
	 */
	public static Set<String> keys(String pattern) {
		Set<String> result = new HashSet<String>();
		Jedis jedis = null;
		try {
			jedis = getResource();
			result = jedis.keys(pattern);
			LOG.debug("keys {}", result);
		} catch (JedisConnectionException e) {
			returnBrokenResource(jedis);
			jedis = null;
		} catch (Exception e) {
			LOG.warn("keys {}", result, e);
		} finally {
			returnResource(jedis);
		}
		return result;

	}

}
