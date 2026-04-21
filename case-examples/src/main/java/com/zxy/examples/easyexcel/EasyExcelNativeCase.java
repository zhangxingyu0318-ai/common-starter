package com.zxy.examples.easyexcel;

import com.alibaba.excel.EasyExcel;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.List;

/**
 * EasyExcel 原生 API 导入导出示例。
 */
public final class EasyExcelNativeCase {

    private EasyExcelNativeCase() {
    }

    public static <T> byte[] exportToBytes(List<T> rows, Class<T> headClass, String sheetName) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        EasyExcel.write(outputStream, headClass)
                .autoCloseStream(false)
                .sheet(sheetName)
                .doWrite(rows);
        return outputStream.toByteArray();
    }

    public static <T> List<T> importFromBytes(byte[] bytes, Class<T> headClass, String sheetName) {
        InputStream inputStream = new ByteArrayInputStream(bytes);
        return EasyExcel.read(inputStream, headClass, null)
                .autoCloseStream(false)
                .sheet(sheetName)
                .doReadSync();
    }
}

