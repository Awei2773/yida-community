package com.waigo.yida.community.log.annotation;

import com.waigo.yida.community.log.enums.UserOption;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * author waigo
 * create 2021-10-08 16:41
 */

/**
 * 进行标注是否需要进行用户操作的日志收集
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface LogUserOpt {
    UserOption value();
}
