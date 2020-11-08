package com.wsz.service;

import com.wsz.pojo.bo.SubmitOrderBo;

/**
 * @author by 完善者
 * @date 2020/9/20 9:29
 * @DESC
 */
public interface OrderService {

    /**
     * 用于创建订单相关信息
     * @param submitOrderBo
     */
    public String createOrder(SubmitOrderBo submitOrderBo);


    /**
     * 修改订单状态
     * @param orderId
     * @param orderStatus
     */
    public void updateOrderStatus(String orderId,Integer orderStatus);

}
