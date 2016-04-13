/**
 * 
 */
package com.air.project.common.dict.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author air
 *
 * 2016年2月3日 下午1:33:13
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface BeanType {
	//数据字典类型
	String value();
	//数据字典名字
	String lable();
	
	
}
