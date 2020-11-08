package com.wsz.enums;

/**
 * @author by 完善者
 * @date 2020/10/4 9:42
 * @DESC 支付方式枚举
 */
public enum PayMethod {
    WEIXIN(1,"微信"),
    ALIPAY(2,"支付宝");

    public final Integer code;
    public final String value;

    PayMethod(Integer code, String value) {
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
