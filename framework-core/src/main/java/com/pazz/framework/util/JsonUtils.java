package com.pazz.framework.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.apachecommons.CommonsLog;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * @author: 彭坚
 * @create: 2018/11/15 17:23
 * @description: Json工具类
 */
@CommonsLog
public final class JsonUtils {

    private static final ObjectMapper mapper = new ObjectMapper();

    static {
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public static <T> T toObject(String jsonStr, Class<T> t) {
        if (jsonStr == null || "".equals(jsonStr)) {
            return null;
        }
        if (t.isInstance(jsonStr)) {
            return (T) jsonStr;
        }
        try {
            return mapper.readValue(jsonStr, t);
        } catch (IOException e) {
            log.error("json parse exception", e);
            throw new RuntimeException("json parse exception", e);
        }
    }

    public static String toJson(Object o) {
        try {
            return mapper.writeValueAsString(o);
        } catch (IOException e) {
            log.error("json parse exception", e);
            throw new RuntimeException("json parse exception", e);
        }
    }

    /**
     * json泛型解析
     */
    public static <T> T toObject(String jsonStr, TypeReference<T> typeReference) {
        if (jsonStr == null || "".equals(jsonStr)) {
            return null;
        }
        try {
            return mapper.readValue(jsonStr, typeReference);
        } catch (IOException e) {
            log.error("json parse exception", e);
            throw new RuntimeException("json parse exception", e);
        }
    }

    /**
     * json JavaType 解析
     */
    public static <T> T toObject(String jsonStr, JavaType type) {
        if (jsonStr == null || "".equals(jsonStr)) {
            return null;
        }
        try {
            return mapper.readValue(jsonStr, type);
        } catch (IOException e) {
            log.error("json parse exception", e);
            throw new RuntimeException("json parse exception", e);
        }
    }

    /**
     * Json Type 类型解析
     */
    public static <T> T toObject(String jsonStr, Type type) {
        return toObject(jsonStr, parseJavaType(type));
    }

    private static List<Class<?>> parseGenericType(Type type) {
        List<Class<?>> rootList = new ArrayList<Class<?>>();
        if (type instanceof ParameterizedType) {
            ParameterizedType pType = (ParameterizedType) type;
            rootList.add((Class<?>) pType.getRawType());
            for (Type at : pType.getActualTypeArguments()) {
                List<Class<?>> childList = parseGenericType(at);
                rootList.addAll(childList);
            }
        } else {
            rootList.add((Class<?>) type);
        }
        return rootList;
    }

    private static JavaType parseJavaType(Type genericParameterType) {
        List<Class<?>> list = parseGenericType(genericParameterType);
        if (list == null || list.size() == 1) {
            return mapper.getTypeFactory().constructType(genericParameterType);
        }
        Class<?>[] classes = list.toArray(new Class[list.size()]);
        Class<?>[] paramClasses = new Class[classes.length - 1];
        System.arraycopy(classes, 1, paramClasses, 0, paramClasses.length);
        JavaType javaType = mapper.getTypeFactory().constructParametrizedType(classes[0], classes[0], paramClasses);
        return javaType;
    }

}
