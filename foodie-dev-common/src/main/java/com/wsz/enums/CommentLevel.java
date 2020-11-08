package com.wsz.enums;

/**
 * @author by 完善者
 * @date 2020/9/20 9:54
 * @DESC 商品评价等级的枚举
 */
public enum CommentLevel {
    GOOD(1,"好评"),
    NORMAL(2,"中评"),
    BAD(3,"差评");

    public final Integer code;
    public final String value;

    CommentLevel(Integer code, String value) {
        this.code = code;
        this.value = value;
    }

    public Integer getCode() {
        return code;
    }

    public String getValue() {
        return value;
    }
}
