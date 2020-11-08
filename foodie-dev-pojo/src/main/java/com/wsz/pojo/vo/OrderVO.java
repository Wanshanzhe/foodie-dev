package com.wsz.pojo.vo;

import com.wsz.pojo.bo.ShopcartBo;

import java.util.List;

public class OrderVO {

    private String orderId;
    private MerchantOrdersVO merchantOrdersVO;

    private List<ShopcartBo> toBeRemovedShopcatdList;

    public List<ShopcartBo> getToBeRemovedShopcatdList() {
        return toBeRemovedShopcatdList;
    }

    public void setToBeRemovedShopcatdList(List<ShopcartBo> toBeRemovedShopcatdList) {
        this.toBeRemovedShopcatdList = toBeRemovedShopcatdList;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public MerchantOrdersVO getMerchantOrdersVO() {
        return merchantOrdersVO;
    }

    public void setMerchantOrdersVO(MerchantOrdersVO merchantOrdersVO) {
        this.merchantOrdersVO = merchantOrdersVO;
    }
}
