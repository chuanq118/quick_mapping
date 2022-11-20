package cn.lqs.quick_mapping.infrastructure.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static cn.lqs.quick_mapping.config.QMConstants.REST_CONTEXT_PATH;

/**
 * @author @lqs
 */

@Retention(RetentionPolicy.RUNTIME)  // runtime annotation.
@Target(ElementType.METHOD)
public @interface LogRequest {

    /**
     * 路径
     */
    String path() default REST_CONTEXT_PATH;

    /**
     * 额外的描述信息
     */
    String comment() default "";

}
