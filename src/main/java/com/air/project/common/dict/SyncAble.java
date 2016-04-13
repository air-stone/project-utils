/**
 * 数据字典同步接口
 */
package com.air.project.common.dict;

import java.util.List;

import com.air.project.common.dict.entity.Dict;

/**
 * @author air
 *
 * 2016年2月3日 下午2:57:02
 */
public interface SyncAble {
	/**
	 *	获取所有数据字典 
	 */
	List<Dict> getAllList();
	
	/**
	 * 保存数据字典
	 * @param dict 字典
	 */
	void save(Dict dict);
}
