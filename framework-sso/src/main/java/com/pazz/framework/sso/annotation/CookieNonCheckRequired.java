package com.pazz.framework.sso.annotation;

import java.lang.annotation.*;

/**
 * @author: 彭坚
 * @create: 2018/11/16 0:38
 * @description: 不校验Cookie注解
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface CookieNonCheckRequired {
}
