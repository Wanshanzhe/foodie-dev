package com.wsz.service.impl;

import com.wsz.enums.OrderStatusEnum;
import com.wsz.enums.YesOrNo;
import com.wsz.mapper.OrderItemsMapper;
import com.wsz.mapper.OrderStatusMapper;
import com.wsz.mapper.OrdersMapper;
import com.wsz.pojo.*;
import com.wsz.pojo.bo.ShopcartBo;
import com.wsz.pojo.bo.SubmitOrderBo;
import com.wsz.pojo.vo.MerchantOrdersVO;
import com.wsz.pojo.vo.OrderVO;
import com.wsz.service.AddressService;
import com.wsz.service.ItemService;
import com.wsz.service.OrderService;
import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author by 完善者
 * @date 2020/9/20 9:32
 * @DESC
 */
@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrdersMapper ordersMapper;

    @Autowired
    private OrderItemsMapper orderItemsMapper;

    @Autowired
    private OrderStatusMapper orderStatusMapper;

    @Autowired
    private AddressService addressService;

    @Autowired
    private ItemService itemService;

    @Autowired
    private Sid sid;

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public OrderVO createOrder(List<ShopcartBo> shopcartList, SubmitOrderBo submitOrderBo) {

        String userId = submitOrderBo.getUserId();
        String addressId = submitOrderBo.getAddressId();
        String itemSpecIds = submitOrderBo.getItemSpecIds();
        Integer payMethod = submitOrderBo.getPayMethod();
        String leftMsg = submitOrderBo.getLeftMsg();
        //包邮费用设置为0
        Integer postAmount  = 0;
        String orderId = sid.nextShort();

        UserAddress userAddress = addressService.queryUserAddres(userId, addressId);

        //1.新订单数据保存
        Orders newOrder = new Orders();
        newOrder.setId(orderId);
        newOrder.setUserId(userId);

        newOrder.setReceiverName(userAddress.getReceiver());
        newOrder.setReceiverMobile(userAddress.getMobile());
        newOrder.setReceiverAddress(userAddress.getProvince() + " "
                                        + userAddress.getCity() + " "
                                        + userAddress.getDistrict() + " "
                                        + userAddress.getDetail());

        newOrder.setPostAmount(postAmount);
        newOrder.setPayMethod(payMethod);
        newOrder.setLeftMsg(leftMsg);
        newOrder.setIsComment(YesOrNo.NO.getCode());
        newOrder.setIsDelete(YesOrNo.NO.getCode());
        newOrder.setCreatedTime(new Date());
        newOrder.setUpdatedTime(new Date());

        //2.根据itemSpecIds保存订单商品信息表
        String itemSpecIdArr[] = itemSpecIds.split(",");
        Integer totalAmount = 0; //商品原价累计
        Integer realPayAmount = 0; //优惠后的实际支付价格累计
        List<ShopcartBo> toBeRomovedShopcatList = new ArrayList<>();
        for (String itemSpecId : itemSpecIdArr){
            ShopcartBo cartItem = getBuyCountsFromShopCart(shopcartList, itemSpecId);
            //整合redis后，商品购买的数量重新从redis的购物车中获取
            int buyCounts = cartItem.getBuyCounts();
            toBeRomovedShopcatList.add(cartItem);
            //2.1根据规格id，查询规格的基本信息，主要获取价格
            ItemsSpec itemsSpec = itemService.queryItemSpecById(itemSpecId);
            totalAmount += itemsSpec.getPriceNormal() * buyCounts;
            realPayAmount += itemsSpec.getPriceDiscount() * buyCounts;
            //2.2 根据商品id，获的商品信息以及商品图片
            String itemId = itemsSpec.getItemId();
            Items item = itemService.queryItemById(itemId);
            String imgUrl = itemService.queryItemMainImgById(itemId);
            //2.3 循环保存订单熟数据到数据库
            String subOrderId = sid.nextShort();
            OrderItems subOrderItem = new OrderItems();
            subOrderItem.setItemId(subOrderId);
            subOrderItem.setId(subOrderId);
            subOrderItem.setOrderId(orderId);
            subOrderItem.setItemId(itemId);
            subOrderItem.setItemName(item.getItemName());
            subOrderItem.setItemImg(imgUrl);
            subOrderItem.setBuyCounts(buyCounts);
            subOrderItem.setItemSpecId(itemSpecId);
            subOrderItem.setItemSpecName(itemsSpec.getName());
            subOrderItem.setPrice(itemsSpec.getPriceDiscount());
            orderItemsMapper.insert(subOrderItem);
            //2.4 在用户提交订单以后，规格表应该扣除库存
            itemService.decreaseItemSpecStock(itemSpecId,buyCounts);
        }

        newOrder.setTotalAmount(totalAmount);
        newOrder.setRealPayAmount(realPayAmount);
        ordersMapper.insert(newOrder);

        //3.保存订单状态表
        OrderStatus waitPayOrderStatus = new OrderStatus();
        waitPayOrderStatus.setOrderId(orderId);
        waitPayOrderStatus.setOrderStatus(OrderStatusEnum.WAIT_PAY.getCode());
        waitPayOrderStatus.setCreatedTime(new Date());
        orderStatusMapper.insert(waitPayOrderStatus);

        // 4. 构建商户订单，用于传给支付中心
        MerchantOrdersVO merchantOrdersVO = new MerchantOrdersVO();
        merchantOrdersVO.setMerchantOrderId(orderId);
        merchantOrdersVO.setMerchantUserId(userId);
        merchantOrdersVO.setAmount(realPayAmount + postAmount);
        merchantOrdersVO.setPayMethod(payMethod);

        //5.构建自定义订单vo
        OrderVO orderVO = new OrderVO();
        orderVO.setOrderId(orderId);
        orderVO.setMerchantOrdersVO(merchantOrdersVO);
        orderVO.setToBeRemovedShopcatdList(toBeRomovedShopcatList);
        return orderVO;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void updateOrderStatus(String orderId, Integer orderStatus) {
        OrderStatus paidStatus = new OrderStatus();
        paidStatus.setOrderId(orderId);
        paidStatus.setOrderStatus(orderStatus);
        paidStatus.setPayTime(new Date());
        orderStatusMapper.updateByPrimaryKeySelective(paidStatus);
    }

    /**
     * 从redis中的购物车里获取商品，目的：处理counts
     * @param shopCartList
     * @param specId
     * @return
     */
    private ShopcartBo getBuyCountsFromShopCart(List<ShopcartBo> shopCartList,String specId){
        for (ShopcartBo cart : shopCartList){
            if (cart.getSpecId().equals(specId)){
                return  cart;
            }
        }
        return null;
    }
}
