package com.zxy.core;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * 通用工具类
 * 使用 FastJSON2 实现深拷贝功能
 *
 * @author zxy
 */
public class CommonUtils {

    /**
     * 深拷贝对象
     * 使用 FastJSON2 的序列化/反序列化方式进行深拷贝
     *
     * @param obj 要拷贝的对象
     * @param <T> 对象类型
     * @return 深拷贝后的对象，如果输入为null则返回null
     * @throws RuntimeException 如果序列化或反序列化失败
     */
    public static <T> T deepCopy(T obj) {
        if (obj == null) {
            return null;
        }
        try {
            // 使用 FastJSON2 进行序列化再反序列化，实现深拷贝
            String jsonStr = JSON.toJSONString(obj);
            return JSON.parseObject(jsonStr, new TypeReference<T>() {});
        } catch (Exception e) {
            throw new RuntimeException("对象深拷贝失败: " + e.getMessage(), e);
        }
    }


    /**
     * 深拷贝集合到指定集合
     * 将源集合的元素深拷贝到指定的目标集合中
     *
     * @param source 源集合
     * @param target 目标集合
     * @param elementType 集合元素类型
     * @param <T> 集合元素类型
     * @param <C> 集合类型
     * @return 目标集合
     * @throws RuntimeException 如果序列化或反序列化失败
     */
    public static <T, C extends Collection<T>> C deepCopyCollection(C source, C target, Class<T> elementType) {
        if (source == null || target == null) {
            return target;
        }

        try {
            Iterator<T> iterator = source.iterator();
            while (iterator.hasNext()) {
                T element = iterator.next();
                T copiedElement = JSON.parseObject(JSON.toJSONString(element), elementType);
                target.add(copiedElement);
            }
            return target;
        } catch (Exception e) {
            throw new RuntimeException("集合深拷贝失败: " + e.getMessage(), e);
        }
    }

    /**
     * 深拷贝 Map
     * 支持 Map 类型的深拷贝
     *
     * @param map 要拷贝的Map
     * @param <K> Map键类型
     * @param <V> Map值类型
     * @return 深拷贝后的Map，如果输入为null则返回null
     * @throws RuntimeException 如果序列化或反序列化失败
     */
    public static <K, V> Map<K, V> deepCopyMap(Map<K, V> map) {
        if (map == null) {
            return null;
        }

        try {
            // 使用 FastJSON2 进行序列化再反序列化，实现深拷贝
            String jsonStr = JSON.toJSONString(map);
            return JSON.parseObject(jsonStr, new TypeReference<HashMap<K, V>>() {});
        } catch (Exception e) {
            throw new RuntimeException("Map深拷贝失败: " + e.getMessage(), e);
        }
    }

    /**
     * 深拷贝数组
     * 支持数组类型的深拷贝
     *
     * @param array 要拷贝的数组
     * @param <T> 数组元素类型
     * @return 深拷贝后的数组，如果输入为null则返回null
     * @throws RuntimeException 如果序列化或反序列化失败
     */
    @SuppressWarnings("unchecked")
    public static <T> T[] deepCopyArray(T[] array) {
        if (array == null) {
            return null;
        }

        try {
            // 使用 FastJSON2 进行序列化再反序列化，实现深拷贝
            String jsonStr = JSON.toJSONString(array);
            return (T[]) JSON.parseObject(jsonStr, array.getClass());
        } catch (Exception e) {
            throw new RuntimeException("数组深拷贝失败: " + e.getMessage(), e);
        }
    }
}

