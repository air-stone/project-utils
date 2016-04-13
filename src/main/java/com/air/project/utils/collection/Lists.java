/**
 * 
 */
package com.air.project.utils.collection;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author air
 *
 * 2016年2月4日 上午11:06:02
 * @see com.google.common.collect.lists
 */
public class Lists {
	
	/**
	 * 数组转化成List
	 * @param 数组
	 * @return
	 */
	public static <T> List<T> asList(final T[]a){
		return new AbstractList<T>() {
			@Override
			public T get(int index) {
				return a[index];
			}
			@Override
			public int size() {
				return a.length;
			}
		
		};
	}
	/**
	 * MAP的key转化为List
	 * @param map
	 * @return
	 */
	public static <K,V> List<K> keyAsList(Map<K,V> map){
		return new ArrayList<K>(map.keySet());
	}
	/**
	 * MAP的value转化为List
	 * @param map
	 * @return
	 */
	public static <K,V> List<V> valueAsList(Map<K,V> map){
		return new ArrayList<V>(map.values());
	}
}
