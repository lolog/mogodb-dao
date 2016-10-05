package net.oicp.wego.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** 
 * @author         lolog
 * @version        V1.0  
 * @Date           2016.07.10
 * @Company        WEGO
 * @Description    MongoDB
*/
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface Collection {
	// 集合名
	public String name() default "";
}
