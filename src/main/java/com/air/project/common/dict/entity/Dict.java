/**
 * 
 */
package com.air.project.common.dict.entity;


import java.io.Serializable;

import com.air.project.common.dict.IDict;

import lombok.Data;

/**
 * @author air
 *
 * 2016年2月3日 下午1:40:59
 */
@Data
public class Dict implements IDict,Serializable,Cloneable{
	
	private static final long serialVersionUID = -7314025820623506939L;
	private long id;
	/**
	 * @see com.air.project.common.dict.entity.DelFlag
	 */
	private long delFlag=DelFlag.NOT_DELETE;
	
	/** 数据字典类型 唯一
	 * @see com.air.project.common.dict.annotations.BeanType
	 */
	private String type;
	/**
	 * 类型中文
	 */
	private String lable;
	
	/** 属性值
	 * @see com.air.project.common.dict.annotations.BeanAttr
	 */
	private long attr;
	private String value;
	private long sortBy;
	private long refAttr;
	private long systemType;
	private String cnName;
	private String enName;
	private String remark;

	public Dict() {
		super();
	}
	
	public static Dict newInstance() {
		return new Dict();
	}
}
