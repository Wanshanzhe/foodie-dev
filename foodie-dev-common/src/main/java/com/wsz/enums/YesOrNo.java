package com.wsz.enums;

/**
 * @author by 完善者
 * @date 2020/9/20 9:54
 * @DESC 是否枚举
 */
public enum YesOrNo {
    NO(0,"否"),
    YES(1,"是");

    public final Integer code;
    public final String value;

    YesOrNo(Integer code, String value) {
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
