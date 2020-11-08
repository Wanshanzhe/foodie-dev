package com.wsz.mapper;

import com.wsz.my.mapper.MyMapper;
import com.wsz.pojo.Orders;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrdersMapper extends MyMapper<Orders> {
}
