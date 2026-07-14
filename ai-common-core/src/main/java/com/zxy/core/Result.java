package com.zxy.core;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 统一 API 响应封装类。
 */
@Data
@NoArgsConstructor
public class Result<T> {
    private T data;
    private String code;
    private String msgCn;
    private String msgEn;

    // 常用操作OK返回 + 提示
    public static Result<Void> ok() {
        return setEnum(ResultEnum.OPERATOR_SUCCESS);
    }

    // 常用操作ok返回 + 提示 + 数据
    public static <T> Result ok(T data) {
        return setEnum(data, ResultEnum.OPERATOR_SUCCESS);
    }

    public Result(String code, String msgCn, String msgEn) {
        this.code = code;
        this.msgCn = msgCn;
        this.msgEn = msgEn;
    }

    public Result(T data, String code, String msgCn, String msgEn) {
        this.data = data;
        this.code = code;
        this.msgCn = msgCn;
        this.msgEn = msgEn;
    }

    private static Result<Void> setEnum(ResultEnum resultEnum) {
        return new Result<>(resultEnum.getCode(), resultEnum.getMsgCn(), resultEnum.getMsgEn());
    }

    private static <T> Result<T> setEnum(T data, ResultEnum resultEnum) {
        return new Result(data, resultEnum.getCode(), resultEnum.getMsgCn(), resultEnum.getMsgEn());
    }

    // 常用操作error返回 + 提示
    public static Result<Void> error() {
        return setEnum(ResultEnum.OPERATOR_ERROR);
    }

    // 常用操作error返回 + 提示 + 错误数据
    public static <T> Result error(T data) {
        return setEnum(data, ResultEnum.OPERATOR_ERROR);
    }

    public static Result<Void> fail(String code, String msgCn, String msgEn) {
        return new Result<>(null, code, msgCn, msgEn);
    }

    public static Result<Void> fail(String msgCn) {
        return fail("400", msgCn, msgCn);
    }

    public static <T> Result<T> success(T data, String msgCn, String msgEn) {
        return new Result<>(data, "200", msgCn, msgEn);
    }

    // 特殊返回，新增/修改/删除/查询 成功 等有使用场景再补充

    private enum ResultEnum {
        OPERATOR_SUCCESS("200", "操作成功!", "operator success."),
        OPERATOR_ERROR("400", "操作失败!", "operator error."),
        INSERT_SUCCESS("200", "插入数据成功!", "insert data success."),
        INSERT_ERROR("400", "插入数据失败!", "insert data error."),
        DELETE_SUCCESS("200", "删除数据成功!", "delete data success."),
        DELETE_ERROR("400", "删除数据失败!", "delete data error."),
        UPDATE_SUCCESS("200", "修改数据成功!", "update data success."),
        UPDATE_ERROR("400", "修改数据失败!", "update data error."),
        SELECT_SUCCESS("200", "查询成功!", "search success."),
        SELECT_ERROR("400", "查询失败!", "search error.");

        private String code;
        private String msgCn;
        private String msgEn;

        ResultEnum(String code, String msgCn, String msgEn) {
            this.code = code;
            this.msgCn = msgCn;
            this.msgEn = msgEn;
        }

        public String getCode() {
            return code;
        }

        public String getMsgCn() {
            return msgCn;
        }

        public String getMsgEn() {
            return msgEn;
        }
    }
}
