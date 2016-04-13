/**
 * 调用事例:
 * private static PropertiesLoader loader=new PropertiesLoader("/jedis/jedis.properties");
   loader.getxxx....
 */
package com.air.project.utils.property;

import java.io.IOException;
import java.io.InputStream;
import java.util.NoSuchElementException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author air
 *
 * 2016年1月26日 下午6:13:36
 */
public class PropertiesLoader {

	private static final Logger LOG = LoggerFactory.getLogger(PropertiesLoader.class);


	private final Properties properties;

	public PropertiesLoader(String... resourcesPaths) {
		properties = loadProperties(resourcesPaths);
	}

	public Properties getProperties() {
		return properties;
	}

	/**
	 * 取出Property，但以System的Property优先,取不到返回空字符串.
	 */
	private String getValue(String key) {
		String systemProperty = System.getProperty(key);
		if (systemProperty != null) {
			return systemProperty;
		}
		if (properties.containsKey(key)) {
	        return properties.getProperty(key);
	    }
	    return "";
	}

	/**
	 * 取出String类型的Property，但以System的Property优先,如果都为Null则抛出异常.
	 */
	public String getProperty(String key) {
		String value = getValue(key);
		if (value == null) {
			throw new NoSuchElementException();
		}
		return value;
	}

	/**
	 * 取出String类型的Property，但以System的Property优先.如果都为Null则返回Default值.
	 */
	public String getProperty(String key, String defaultValue) {
		String value = getValue(key);
		return value != null ? value : defaultValue;
	}

	/**
	 * 取出Integer类型的Property，但以System的Property优先.如果都为Null或内容错误则抛出异常.
	 */
	public Integer getInteger(String key) {
		String value = getValue(key);
		if (value == null) {
			throw new NoSuchElementException();
		}
		return Integer.valueOf(value);
	}

	/**
	 * 取出Long类型的Property，但以System的Property优先.如果都为Null或内容错误则抛出异常.
	 */
	public Long getLongValue(String key) {
		String value = getValue(key);
		if (value == null) {
			throw new NoSuchElementException();
		}
		return Long.valueOf(value);
	}
	/**
	 * 取出Integer类型的Property，但以System的Property优先.如果都为Null则返回Default值，如果内容错误则抛出异常
	 */
	public Integer getInteger(String key, Integer defaultValue) {
		String value = getValue(key);
		return value != null ? Integer.valueOf(value) : defaultValue;
	}

	/**
	 * 取出Double类型的Property，但以System的Property优先.如果都为Null或内容错误则抛出异常.
	 */
	public Double getDouble(String key) {
		String value = getValue(key);
		if (value == null) {
			throw new NoSuchElementException();
		}
		return Double.valueOf(value);
	}

	/**
	 * 取出Double类型的Property，但以System的Property优先.如果都为Null则返回Default值，如果内容错误则抛出异常
	 */
	public Double getDouble(String key, Integer defaultValue) {
		String value = getValue(key);
		return value != null ? Double.valueOf(value) : defaultValue;
	}

	/**
	 * 取出Boolean类型的Property，但以System的Property优先.如果都为Null抛出异常,如果内容不是true/false则返回false.
	 */
	public Boolean getBoolean(String key) {
		String value = getValue(key);
		if (value == null) {
			throw new NoSuchElementException();
		}
		return Boolean.valueOf(value);
	}

	/**
	 * 取出Boolean类型的Property，但以System的Property优先.如果都为Null则返回Default值,如果内容不为true/false则返回false.
	 */
	public Boolean getBoolean(String key, boolean defaultValue) {
		String value = getValue(key);
		return value != null ? Boolean.valueOf(value) : defaultValue;
	}

	private Properties loadProperties(String... resourcesPaths) {
		Properties props = new Properties();
		for (String location : resourcesPaths) {
			LOG.debug("Loading properties file from:" + location);
			InputStream is = null;
			try {
				is = PropertiesLoader.class.getResourceAsStream(location);
				props.load(is);
			} catch (IOException ex) {
				LOG.info("Could not load properties from path:" + location + ", " + ex.getMessage());
			} finally {
				try {
					is.close();
				} catch (IOException e) {
				}
			}
		}
		return props;
	}
}
