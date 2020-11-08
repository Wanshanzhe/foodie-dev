package com.wsz.enums;

/**
 * @author by 完善者
 * @date 2020/10/4 14:20
 * @DESC
 */
public enum OrderStatusEnum {

    WAIT_PAY(10,"待付款"),
    WAIT_DELIVER(20,"已付款，代发货"),
    WAIT_RECEIVE(30,"已发货，待收货"),
    SUCCESS(40,"交易成功"),
    CLOSE(50,"交易关闭");

    public final Integer code;
    public final String value;

    OrderStatusEnum(Integer code, String value) {
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
