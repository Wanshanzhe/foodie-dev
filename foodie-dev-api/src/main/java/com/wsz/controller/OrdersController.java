package com.wsz.controller;

import com.wsz.enums.OrderStatusEnum;
import com.wsz.enums.PayMethod;
import com.wsz.pojo.bo.ShopcartBo;
import com.wsz.pojo.bo.SubmitOrderBo;
import com.wsz.service.OrderService;
import com.wsz.utils.IMOOCJSONResult;
import com.wsz.utils.JsonUtils;
import com.wsz.utils.RedisOperator;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Api(value = "订单相关", tags = {"订单相关的api接口"})
@RequestMapping("orders")
@RestController
public class OrdersController extends BaseController{

    final static Logger logger = LoggerFactory.getLogger(OrdersController.class);

    @Autowired
    private RedisOperator redisOperator;

    @Autowired
    private OrderService orderService;

    @ApiOperation(value = "用户下单", notes = "用户下单", httpMethod = "POST")
    @PostMapping("/create")
    public IMOOCJSONResult create(@RequestBody SubmitOrderBo submitOrderBo, HttpServletRequest request, HttpServletResponse response) {
        if (submitOrderBo.getPayMethod() != PayMethod.WEIXIN.getCode() && submitOrderBo.getPayMethod() != PayMethod.ALIPAY.getCode()){
            return IMOOCJSONResult.errorMsg("支付方式不支持!");
        }

        String shopcartJson = redisOperator.get(FOODIE_SHOPCART + ":" + submitOrderBo.getUserId());
        if (StringUtils.isBlank(shopcartJson)){
            return IMOOCJSONResult.errorMsg("购物车数据不正确");
        }

        List<ShopcartBo> shopcartList = JsonUtils.jsonToList(shopcartJson,ShopcartBo.class);

        //1.创建订单
        String orderId = orderService.createOrder(shopcartList,submitOrderBo);
        //2.创建订单以后，移除购物车中已经结算（已提交）的商品
        //TODO 整合redis之后，完善购物车中的已结算商品清除，并且同步到前端的cookie
//        CookieUtils.setCookie(request,response,FOODIE_SHOPCART,"",true);

        //3.向支付中心发送当前订单，用于保存支付中心的订单数据
        return IMOOCJSONResult.ok(orderId);
    }


    @PostMapping("notifyMerchantOrderPaid")
    public Integer notifyMerchantOrderPaid(String merchantOrderId){
        orderService.updateOrderStatus(merchantOrderId, OrderStatusEnum.WAIT_DELIVER.getCode());
        return HttpStatus.OK.value();
    }
}
