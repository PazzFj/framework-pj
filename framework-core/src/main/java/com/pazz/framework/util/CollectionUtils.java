package com.pazz.framework.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.lang.reflect.Method;
import java.util.*;

/**
 * @author: 彭坚
 * @create: 2018/11/16 0:02
 * @description: 集合常用类
 */
public class CollectionUtils extends org.springframework.util.CollectionUtils {

    private static final Log logger = LogFactory.getLog(CollectionUtils.class);

    /**
     * 拆分List为固定大小的多个集合
     */
    public static <T> List<List<T>> splitListBySize(List<T> values, int size) {
        if (values == null || values.size() == 0) {
            return null;
        }

        List<List<T>> results = new ArrayList<List<T>>();

        for (int i = 0; i < values.size(); ++i) {
            if (i % size == 0) {
                List<T> result = new ArrayList<T>();
                results.add(result);
            }

            results.get(i / size).add(values.get(i));
        }

        return results;
    }

    /**
     * 获取集合PROPERTY集合
     */
    public static <K, V> Set<K> asSet(final java.util.Collection<V> coll, final Class<K> keyType, final String keyMethodName) {
        if (CollectionUtils.isEmpty(coll)) {
            return new HashSet<K>(0);
        }

        final Set<K> set = new LinkedHashSet<K>(coll.size());

        try {
            // return the Method to invoke to get the key for the map

            for (final V value : coll) {

                Object object;
                Method method = value.getClass().getMethod(keyMethodName);
                object = method.invoke(value);
                @SuppressWarnings("unchecked") final K key = (K) object;
                set.add(key);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        return set;
    }

    /**
     * 集合转MAP
     */
    public static <K, V> Map<K, V> asMap(final java.util.Collection<V> coll, final Class<K> keyType, final Class<V> valueType, final String keyMethodName) {
        if (CollectionUtils.isEmpty(coll)) {
            return new LinkedHashMap<K, V>(0);
        }

        final Map<K, V> map = new LinkedHashMap<K, V>(coll.size());

        try {
            // return the Method to invoke to get the key for the map
            Method method = valueType.getMethod(keyMethodName);

            for (final V value : coll) {

                Object object;
                object = method.invoke(value);
                @SuppressWarnings("unchecked") final K key = (K) object;
                map.put(key, value);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        return map;
    }

    /**
     * 集合转LIST
     */
    public static <V> List<V> asList(final java.util.Collection<V> coll) {

        if (CollectionUtils.isEmpty(coll)) {
            return new ArrayList<V>(0);
        }

        final List<V> list = new ArrayList<V>();

        for (final V value : coll) {
            if (value != null) {
                list.add(value);
            }
        }

        return list;
    }

}
