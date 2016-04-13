/**
 * 配置管理器
 * 获取所有Dict子类
 * 如果有syncAble实现，可以同步到数据库
 */
package com.air.project.common.dict;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.air.project.common.dict.annotations.BeanAttr;
import com.air.project.common.dict.annotations.BeanType;
import com.air.project.common.dict.entity.DelFlag;
import com.air.project.common.dict.entity.Dict;
import com.air.project.utils.collection.Lists;
import com.air.project.utils.string.StringUtils;
import com.google.common.collect.Maps;

/**
 * @author air
 *
 * 2016年2月3日 下午2:41:54
 */
public final class ConfigManager {

	/**
	 * 数据字典缓存
	 * key为beanType中的Value唯一
	 */
	private static Map<String, Map<Long,Dict>> dictCache = Maps.newHashMap();
	
	/**
	 * 数据同步接口
	 */
	private static SyncAble syncAble;
	
	private ConfigManager(){
		
	}
	private static boolean hasSync(){
		return syncAble!=null;
	}
	
	/**
	 * 从数据库初始化
	 * @param syncAble
	 */
	public static void init(SyncAble syncAble){
		ConfigManager.syncAble=syncAble;
		if(hasSync()){
			List<Dict> list=syncAble.getAllList();
			for(Dict d:list){
				trans(d);
			}
		}
	}
	
	
	/**
	 * 获取中文名
	 * @param cls 类
	 * @param attrId 属性
	 * @return 中文名
	 */
	public static <T extends Dict> String getCnName(Class<T> cls, long attrId) {
		Dict d = getDict(cls, attrId);
		if (d != null) {
			return d.getCnName();
		}
		return null;
	}
	/**
	 * 获取值
	 * @param cls 类
	 * @param attrId 属性
	 * @return 值
	 */
	public static <T extends Dict> String getValue(Class<T> cls, long attrId) {
		Dict d = getDict(cls, attrId);
		if (d != null) {
			return d.getValue();
		}
		return null;
	}
	/**
	 * 获取英文名
	 * @param cls 类
	 * @param attrId 属性
	 * @return 英文名
	 */
	public static <T extends Dict> String getEnName(Class<T> cls, long attrId) {
		Dict d = getDict(cls, attrId);
		if (d != null) {
			 return d.getEnName();
		}
		return null;
	}
	/**
	 * 获取备注
	 * @param cls 类
	 * @param attrId 属性
	 * @return 备注
	 */
	public static <T extends Dict> String getRemark(Class<T> cls, long attrId) {
		Dict d = getDict(cls, attrId);
		if (d != null) {
			 return d.getRemark();
		}
		return null;
	}
	
	/**
	 * 获取类型
	 * @param cls
	 * @return 集合
	 */
	public static <T extends Dict> List<Dict> getList(Class<T> cls) {
		String type = getType(cls);
		check(type, cls, 0);
		if (dictCache.containsKey(type)) {
			Map<Long, Dict> tmp = dictCache.get(type);
			return new ArrayList<Dict>(tmp.values());
		}
		return Collections.emptyList();
	}
	
	/**
	 * 获取部分或者所有
	 * @param cls 类
	 * @param includeIds 包含属性
	 * @return 部分或者所有属性
	 */
	public static <T extends Dict> List<Dict> getList(Class<T> cls,Long... includeIds) {
		List<Dict> list = getList(cls);
		if(includeIds==null){
			return list;
		}else{
			List<Long> attList=Lists.asList(includeIds);
			for(int i=list.size()-1;i>=0;i--){
				Dict d = list.get(i);
				if(!attList.contains(d.getAttr())){
					list.remove(i);
				}
			}
		}
		return list;
	}
	
	
	/**
	 * 获取数据字典
	 * @param cls
	 * @param attrId
	 * @return 类
	 */
	public static <T extends Dict> Dict getDict(Class<T> cls, long attrId) {
		String type = getType(cls);
		check(type, cls, attrId);
		if (dictCache.containsKey(type)) {
			Map<Long, Dict> tmp = dictCache.get(type);
			return tmp.get(attrId);
		}
		return null;
	}
	
	
	
	/***********以下为私有方法***************/
	
	private static void trans(Dict d) {
		String type = d.getType();
		if (!StringUtils.isEmpty(type)) {
			Map<Long, Dict> tmp = dictCache.get(type);
			if (tmp == null || tmp.isEmpty()) {
				dictCache.put(type, new HashMap<Long, Dict>());
			}
			long atrr = d.getAttr();
			if (atrr!=0) {
				dictCache.get(type).put(d.getAttr(), d);
			}
		}
	}
	/**
	 * 获取类型
	 * @param cls 类
	 * @return 类型
	 */
	private static <T extends Dict> String getType(Class<T> cls) {
		if (cls.isAnnotationPresent(BeanType.class)) {
			BeanType beanType = (BeanType) cls.getAnnotation(BeanType.class);
			return beanType.value();
		}
		return null;
	}
	private static <T extends Dict> String getLable(Class<T> cls) {
		if (cls.isAnnotationPresent(BeanType.class)) {
			BeanType beanType = (BeanType) cls.getAnnotation(BeanType.class);
			String lable = beanType.lable();
			return lable;
		}
		return null;
	}
	/**
	 * 检测并放入缓存或者数据库
	 * @param type 类型
	 * @param cls 类
	 * @param attrId 属性
	 */
	private static <T extends Dict> void check(String type, Class<T> cls, long attrId) {
		if (dictCache.containsKey(type)) {
			if (dictCache.get(type).containsKey(attrId)) {
				return;
			}
		}else{
			dictCache.put(type, new HashMap<Long, Dict>());
		}
		Map<Long, Dict> tmp = dictCache.get(type);
		String lable = getLable(cls);
		Field[] at = cls.getDeclaredFields();
		for (Field f : at) {
			f.setAccessible(true);
			if (f.isAnnotationPresent(BeanAttr.class)) {
				try {
					long attr = f.getLong(cls);
					if (!tmp.containsKey(attr)) {
						BeanAttr ba = (BeanAttr) f.getAnnotation(BeanAttr.class);
						Dict d = Dict.newInstance();
						d.setAttr(attr);
						d.setType(type);
						d.setLable(lable);
						d.setValue(ba.value());
						d.setSortBy(ba.sortBy());
						d.setEnName(ba.enName());
						d.setCnName(ba.cnName());
						d.setRefAttr(ba.refAttr());
						d.setSystemType(ba.systemType());
						d.setRemark(ba.remark());
						dictCache.get(type).put(attr, d);
						// insert to DB
						if(hasSync()){
							syncAble.save(d);
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	
	public static void main(String[] args) {
		List<Dict> list=getList(DelFlag.class);
		list.forEach(u->System.out.println(u.getCnName()));
	}
}
