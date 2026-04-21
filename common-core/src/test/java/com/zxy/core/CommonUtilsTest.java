package com.zxy.core;

import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * CommonUtils 工具类测试
 */
public class CommonUtilsTest {

    /**
     * 测试 deepCopy 方法
     */
    @Test
    public void testDeepCopy() {
        // 测试基本对象
        String original = "Hello World";
        String copy = CommonUtils.deepCopy(original);
        assertEquals(original, copy);
        assertNotSame(original, copy); // 确保是不同的对象实例

        // 测试 null
        assertNull(CommonUtils.deepCopy(null));

        // 测试复杂对象
        Map<String, Object> originalMap = new HashMap<>();
        originalMap.put("name", "test");
        originalMap.put("value", 123);
        List<String> list = Arrays.asList("a", "b", "c");
        originalMap.put("list", list);

        Map<String, Object> copiedMap = CommonUtils.deepCopy(originalMap);
        assertEquals(originalMap, copiedMap);
        assertNotSame(originalMap, copiedMap);
        assertNotSame(originalMap.get("list"), copiedMap.get("list"));
    }

    /**
     * 测试 deepCopyCollection 方法
     */
    @Test
    public void testDeepCopyCollection() {
        // 测试 List
        List<String> originalList = Arrays.asList("a", "b", "c");
        List<String> copiedList = CommonUtils.deepCopyCollection(originalList, new ArrayList<>(), String.class);
        assertEquals(originalList, copiedList);
        assertNotSame(originalList, copiedList);

        // 测试 Set
        Set<String> originalSet = new HashSet<>(Arrays.asList("x", "y", "z"));
        Set<String> copiedSet = CommonUtils.deepCopyCollection(originalSet, new HashSet<>(), String.class);
        assertEquals(originalSet, copiedSet);
        assertNotSame(originalSet, copiedSet);

        // 测试 LinkedList
        LinkedList<String> originalLinkedList = new LinkedList<>(Arrays.asList("1", "2", "3"));
        LinkedList<String> copiedLinkedList = CommonUtils.deepCopyCollection(originalLinkedList, new LinkedList<>(), String.class);
        assertEquals(originalLinkedList, copiedLinkedList);
        assertNotSame(originalLinkedList, copiedLinkedList);
        assertTrue(copiedLinkedList instanceof LinkedList);

        // 测试 null
        List<String> copiedNull = CommonUtils.deepCopyCollection(null, new ArrayList<>(), String.class);
        assertNotNull(copiedNull);
        assertTrue(copiedNull.isEmpty());
    }

    /**
     * 测试 deepCopyCollection(C source, C target, Class<T> elementType) 方法
     */
    @Test
    public void testDeepCopyCollectionWithTarget() {
        // 测试 List 到 List 的拷贝
        List<String> sourceList = Arrays.asList("hello", "world", "test");
        List<String> targetList = new ArrayList<>();
        List<String> resultList = CommonUtils.deepCopyCollection(sourceList, targetList, String.class);

        assertEquals(sourceList.size(), resultList.size());
        assertEquals(sourceList, resultList);
        assertNotSame(sourceList, resultList);
        assertSame(targetList, resultList); // 应该返回同一个目标集合对象

        // 测试 Set 到 Set 的拷贝（source 与 target 同泛型）
        Set<String> sourceSet = new HashSet<>(Arrays.asList("a", "b", "c"));
        Set<String> targetSet2 = new HashSet<>();
        Set<String> resultSet2 = CommonUtils.deepCopyCollection(sourceSet, targetSet2, String.class);

        assertEquals(sourceSet.size(), resultSet2.size());
        assertTrue(resultSet2.containsAll(sourceSet));
        assertSame(targetSet2, resultSet2);

        // 测试 null 值处理
        List<String> resultWhenSourceNull = CommonUtils.deepCopyCollection(null, new ArrayList<>(), String.class);
        assertNotNull(resultWhenSourceNull);
        assertTrue(resultWhenSourceNull.isEmpty()); // 当 source 为 null 时，应该返回空的 target

        List<String> resultWhenTargetNull = CommonUtils.deepCopyCollection(new ArrayList<>(), null, String.class);
        assertNull(resultWhenTargetNull); // 当 target 为 null 时，应该返回 null
    }

    /**
     * 测试 deepCopyMap 方法
     */
    @Test
    public void testDeepCopyMap() {
        Map<String, Integer> originalMap = new HashMap<>();
        originalMap.put("one", 1);
        originalMap.put("two", 2);
        originalMap.put("three", 3);

        Map<String, Integer> copiedMap = CommonUtils.deepCopyMap(originalMap);
        assertEquals(originalMap, copiedMap);
        assertNotSame(originalMap, copiedMap);

        // 测试 null
        assertNull(CommonUtils.deepCopyMap(null));
    }

    /**
     * 测试 deepCopyArray 方法
     */
    @Test
    public void testDeepCopyArray() {
        String[] originalArray = {"hello", "world", "test"};
        String[] copiedArray = CommonUtils.deepCopyArray(originalArray);
        assertArrayEquals(originalArray, copiedArray);
        assertNotSame(originalArray, copiedArray);

        // 测试 null
        assertNull(CommonUtils.deepCopyArray(null));
    }
}

