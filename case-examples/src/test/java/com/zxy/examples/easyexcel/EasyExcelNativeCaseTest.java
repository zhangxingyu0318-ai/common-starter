package com.zxy.examples.easyexcel;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class EasyExcelNativeCaseTest {

    @Test
    public void testEasyExcelNativeRoundTrip() {
        List<UserExcelRow> rows = Arrays.asList(
                new UserExcelRow("张三", 18),
                new UserExcelRow("李四", 20)
        );

        byte[] bytes = EasyExcelNativeCase.exportToBytes(rows, UserExcelRow.class, "用户");
        List<UserExcelRow> imported = EasyExcelNativeCase.importFromBytes(bytes, UserExcelRow.class, "用户");

        assertTrue(bytes.length > 0);
        assertEquals(rows, imported);
    }
}

