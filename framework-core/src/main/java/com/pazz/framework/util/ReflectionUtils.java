package com.pazz.framework.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * @author: 彭坚
 * @create: 2018/11/19 15:30
 * @description: 反射工具类
 */
public class ReflectionUtils extends org.springframework.util.ReflectionUtils {

    /**
     * 属性复制
     * @param source 源对象
     * @param target 目标对象
     * @param nullValueCopy 是否复制null值
     */
    public static void copyProperties(Object source, Object target, boolean nullValueCopy){
        Method[] methods = source.getClass().getMethods();
        for (Method method : methods){
            if (method.getName().startsWith("get")){
                try {
                    Object value = method.invoke(source, new Object[0]);
                    //如果value为null, 不允许null值复制,则不进行复制
                    if (value == null && !nullValueCopy){
                        continue;
                    }
                    String setMethodName = method.getName().replaceFirst("get", "set");
                    Method setMethod = target.getClass().getMethod(setMethodName, method.getReturnType());
                    setMethod.invoke(target, value);
                } catch (Exception e) {
                    //do nothing
                }
            }
        }
    }

    /**
     * 属性复制
     * @param source 源对象
     * @param target 目标对象
     */
    public static void copyProperties(Object source, Object target){
        copyProperties(source, target, true);
    }

    /**
     * 获取对象属性值
     * @param obj 对象
     * @param property 属性名称
     * @return 属性值
     */
    public static Object getPropertyValue(Object obj, String property){
        try {
            Field field = obj.getClass().getDeclaredField(property);
            makeAccessible(field);
            return getField(field, obj);
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 设置属性对象值
     * @param obj 对象
     * @param property 属性名称
     * @param value 属性值
     */
    public static void setPropertyValue(Object obj, String property, Object value){
        try {
            Field field = obj.getClass().getDeclaredField(property);
            makeAccessible(field);
            setField(field, obj, value);
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

}
