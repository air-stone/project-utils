/**
 * 
 */
package com.air.project.common.dict.entity;

import com.air.project.common.dict.annotations.BeanAttr;
import com.air.project.common.dict.annotations.BeanType;

/**
 * @author air
 *
 *         2016年2月3日 下午1:49:28
 */
@BeanType(value = "del_flag", lable = "删除标记")
public class DelFlag extends Dict {
	
	private static final long serialVersionUID = -6612149292819134089L;

	@BeanAttr(cnName="未删除")
	public static final long NOT_DELETE=1L;

	@BeanAttr(cnName="已删除")
	public static final long DELETED=2L;
}
