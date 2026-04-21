package com.zxy.examples.easyexcel;

import com.alibaba.excel.annotation.ExcelProperty;

import java.util.Objects;

/**
 * EasyExcel 行模型示例。
 */
public class UserExcelRow {

    @ExcelProperty("姓名")
    private String name;

    @ExcelProperty("年龄")
    private Integer age;

    public UserExcelRow() {
    }

    public UserExcelRow(String name, Integer age) {
        this.name = name;
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof UserExcelRow)) {
            return false;
        }
        UserExcelRow that = (UserExcelRow) obj;
        return Objects.equals(name, that.name) && Objects.equals(age, that.age);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, age);
    }
}

