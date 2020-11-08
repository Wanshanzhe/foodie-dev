package com.wsz.service;

import com.wsz.pojo.bo.ShopcartBo;
import com.wsz.pojo.bo.SubmitOrderBo;
import com.wsz.pojo.vo.OrderVO;

import java.util.List;

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
    public OrderVO createOrder(List<ShopcartBo> shopcartList, SubmitOrderBo submitOrderBo);


    /**
     * 修改订单状态
     * @param orderId
     * @param orderStatus
     */
    public void updateOrderStatus(String orderId,Integer orderStatus);

}
