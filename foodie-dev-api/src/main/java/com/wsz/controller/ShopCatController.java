package com.wsz.controller;

import com.wsz.pojo.bo.ShopcartBo;
import com.wsz.utils.IMOOCJSONResult;
import com.wsz.utils.JsonUtils;
import com.wsz.utils.RedisOperator;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * @author by 完善者
 * @date 2020/9/26 14:01
 * @DESC
 */

@Api(value = "购物车接口controller",tags = {"购物车接口相关的api"})
@RequestMapping("shopcart")
@RestController
public class ShopCatController extends BaseController{

    @Autowired
    private RedisOperator redisOperator;

    final static Logger logger = LoggerFactory.getLogger(ShopCatController.class);

    @ApiOperation(value = "添加商品到购物车",notes = "添加商品到购物车",httpMethod = "POST")
    @PostMapping("/add")
    public IMOOCJSONResult add(
            @RequestParam String userId,
            @RequestBody ShopcartBo shopcartBo,
            HttpServletRequest request,
            HttpServletResponse response
    ){
        if (StringUtils.isBlank("userId")){
            return IMOOCJSONResult.errorMsg("");
        }

        logger.info("shopcartBo:{}",shopcartBo);
        //前端用户在登录的情况下，添加商品到购物车，会同时在后端同步购物车到redis缓存
        //需要判断当前购物车中包含已经存在的商品，如果存在则累加购买数量
        String shopcatJson = redisOperator.get(FOODIE_SHOPCART + ":" + userId);
        List<ShopcartBo> shopcartList = null;
        if (StringUtils.isNotBlank(shopcatJson)){
            //redis中已经有购物车了
            shopcartList = JsonUtils.jsonToList(shopcatJson,ShopcartBo.class);
            //判断购物车中是否已经有商品，如果有的话counts累加
            boolean isHaving = false;
            for (ShopcartBo sc : shopcartList) {
                String tmpSpecId = sc.getSpecId();
                if (tmpSpecId.equals(shopcartBo.getSpecId())){
                    sc.setBuyCounts(sc.getBuyCounts() + shopcartBo.getBuyCounts());
                    isHaving = true;
                }
            }
            if (!isHaving){
                shopcartList.add(shopcartBo);
            }
        }else {
            //redis中没有购物车
            shopcartList = new ArrayList<>();
            //直接添加到购物车中
            shopcartList.add(shopcartBo);
        }
        //覆盖现有redis中的购物车
        redisOperator.set(FOODIE_SHOPCART + ":" + userId,JsonUtils.objectToJson(shopcartList));
        return IMOOCJSONResult.ok();
    }

    @ApiOperation(value = "从购物车中删除商品",notes = "从购物车中删除商品",httpMethod = "POST")
    @PostMapping("/del")
    public IMOOCJSONResult add(
            @RequestParam String userId,
            @RequestParam String itemSpecId,
            HttpServletRequest request,
            HttpServletResponse response
    ){
        if (StringUtils.isBlank("userId") || StringUtils.isBlank(itemSpecId)){
            return IMOOCJSONResult.errorMsg("参数不能为空");
        }
        //用户在页面删除购物车中的商品数据，如果此时用户已经登录，则需要同步删除redis后端购物车中的商品
        String shopCartJson = redisOperator.get(FOODIE_SHOPCART + ":" + userId);
        if (StringUtils.isNotBlank(shopCartJson)){
            //redis中已经存在购物车了
            List<ShopcartBo> shopcartBoList = JsonUtils.jsonToList(shopCartJson, ShopcartBo.class);
            for (ShopcartBo sc : shopcartBoList){
                String tmpSpecId = sc.getSpecId();
                if (tmpSpecId.equals(itemSpecId)){
                    shopcartBoList.remove(sc);
                    break;
                }
            }
            //覆盖现有的redis的购物车
            redisOperator.set(FOODIE_SHOPCART + ":" + userId,JsonUtils.objectToJson(shopcartBoList));
        }
        return IMOOCJSONResult.ok();
    }

}
