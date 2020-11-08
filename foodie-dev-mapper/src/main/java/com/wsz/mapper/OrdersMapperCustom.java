package com.wsz.mapper;

import com.wsz.pojo.OrderStatus;
import com.wsz.pojo.vo.MyOrdersVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @author by 完善者
 * @date 2020/10/11 9:39
 * @DESC
 */
public interface OrdersMapperCustom {

    public List<MyOrdersVO> queryMyOrders(@Param("paramsMap") Map<String,Object> map);

    public int getMyOrderStatusCounts(@Param("paramsMap") Map<String,Object> map);

    public List<OrderStatus> getMyOrderTrend(@Param("paramsMap") Map<String,Object> map);
}
