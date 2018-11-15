package com.pazz.framework.web.security;

import java.lang.annotation.*;

/**
 * @author: 彭坚
 * @create: 2018/11/16 0:08
 * @description: 权限控制注解，使用该注解表明无需判断权限
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface SecurityNonCheckRequired {
}
