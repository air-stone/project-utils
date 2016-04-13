/**
 * demo 暂未使用
 */
package com.air.project.common.dict.init;

import java.util.Collections;
import java.util.List;

import com.air.project.common.dict.SyncAble;
import com.air.project.common.dict.entity.Dict;

/**
 * @author air
 * @Deprecated 实现demo
 * 2016年2月3日 下午2:45:34
 */
@Deprecated
public class DBSyncDict  implements SyncAble{

//	private static DictService dictService=application.getBean(xxxx);
	
	public List<Dict> getAllList() {
		return Collections.emptyList();
	}

	
	public void save(Dict dict) {
		//使数据字典持久化
		throw new UnsupportedOperationException();
	}
	
}
