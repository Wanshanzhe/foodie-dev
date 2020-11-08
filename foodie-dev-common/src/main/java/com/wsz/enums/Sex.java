package com.wsz.enums;

/**
 * @author by 完善者
 * @date 2020/8/10 23:01
 * @DESC
 */

/**
 * 性别枚举
 */
public enum Sex {
    WOMAN0(0,"女"),
    MAN(1,"男"),
    SECRET(2,"保密");

    public final Integer code;
    public final String value;

    Sex(Integer code, String value) {
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
