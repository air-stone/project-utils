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
 * 2016年2月3日 下午1:37:23
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface BeanAttr {
	/** 对应Dictionary对象中value */
	String value() default "";

	/** 排序 */
	long sortBy() default 0;

	/** 对应Dictionary对象中cnName */
	String cnName() default "";

	/** 对应Dictionary对象中enName;如果为空,默认使用attr值 */
	String enName() default "";
	
	String remark() default "";

//	/** 引用的Class */
//	Class<? extends Dict> refClass() default Dict.class;

	/** 引用的Class所对应的属性 */
	long refAttr() default 0;
	
	/** 所属系统类型*/
	long systemType() default 0;
}
