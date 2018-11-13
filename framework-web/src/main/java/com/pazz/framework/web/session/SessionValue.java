package com.pazz.framework.web.session;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @author: Peng Jian
 * @create: 2018/11/13 13:52
 * @description: 标注允许被放入session的类型
 */
@Target(TYPE)
@Retention(RUNTIME)
public @interface SessionValue {

}
