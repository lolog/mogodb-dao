package net.oicp.wego.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** 
 * @author         lolog
 * @version        V1.0  
 * @Date           2016年7月8日
 * @Company        WEGO
 * @Description    创建索引
*/
@Inherited   // 子类会被继承
@Documented  // 文档化
@Retention(RetentionPolicy.RUNTIME) // 运行时可以获取
@Target({ElementType.METHOD, ElementType.FIELD}) 
public @interface Index {
	// 是否是索引
	public boolean key() default true;
	// 唯一索引
	public boolean unique() default false;
    // 索引方式
    public int order() default 1;
    // 索引名字
    public String name() default "";
    // ObjectId声明
    public boolean id() default false;
    // omission
    public boolean omission() default true;
    // 后台方式创建索引
    public boolean background() default true;
    // 文档中不存在的字段数据不启用索引。设置为true,索引字段中不会查询出不包含对应字段的文档
    public boolean sparse() default false;
}
