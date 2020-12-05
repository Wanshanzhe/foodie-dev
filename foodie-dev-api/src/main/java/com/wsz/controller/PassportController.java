package com.wsz.controller;

import com.wsz.pojo.Users;
import com.wsz.pojo.bo.ShopcartBo;
import com.wsz.pojo.bo.UserBO;
import com.wsz.pojo.vo.UsersVO;
import com.wsz.service.UserService;
import com.wsz.utils.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * @author by 完善者
 * @date 2020/8/7 23:33
 * @DESC
 */
@Api(value = "注册登录",tags = {"用于注册登录的相关接口"})
@RestController
@RequestMapping("passport")
public class PassportController extends BaseController{

    @Autowired
    private UserService userService;

    @Autowired
    private RedisOperator redisOperator;

    @ApiOperation(value = "用户名是否存在",notes = "用户名是否存在",httpMethod = "GET")
    @GetMapping("/usernameIsExist")
    public IMOOCJSONResult usernameIsExist(@RequestParam String username){
        //1判断用户名不能为空
        if(StringUtils.isBlank(username)){
            return IMOOCJSONResult.errorMsg("用户名不能为空");
        }
        //2.查找注册的用户名是否存在
        boolean isExist = userService.queryUsernameIsExist(username);
        if (isExist){
            return IMOOCJSONResult.errorMsg("用户已经存在");
        }
        //3.请求成功，用户名没有重复
        return IMOOCJSONResult.ok();
    }

    @ApiOperation(value = "用户注册",notes = "用户注册",httpMethod = "POST")
    @PostMapping("/regist")
    public IMOOCJSONResult regist(@RequestBody UserBO userBO,HttpServletRequest request, HttpServletResponse response){

        String username = userBO.getUsername();
        String password = userBO.getPassword();
        String confirmPassword = userBO.getConfirmPassword();

        //0.判断用户名和密码必须不为空
        if (StringUtils.isBlank(username) || StringUtils.isBlank(password) || StringUtils.isBlank(confirmPassword)){
            return IMOOCJSONResult.errorMsg("用户名或者密码不能为空");
        }
        //1.查询用户名是否存在
        boolean isExist = userService.queryUsernameIsExist(userBO.getUsername());
        if (isExist){
            return IMOOCJSONResult.errorMsg("用户名已经存在");
        }
        //2.密码长度不能少于6位
        if (password.length() < 6){
            return IMOOCJSONResult.errorMsg("密码长度不能小于6");
        }
        //3.判断两次密码是否一致
        if (!password.equals(confirmPassword)){
            return IMOOCJSONResult.errorMsg("两次密码输入不一致");
        }
        //4.实现注册
        Users usersResult = userService.createUser(userBO);

//        usersResult = setNullProperty(usersResult);

        //实现用户的redis会话
        UsersVO usersVO = conventUsersVO(usersResult);

        CookieUtils.setCookie(request,response,"user", JsonUtils.objectToJson(usersVO),true);

        //生成用户token，存入redis会话
        //同步购物车数据
        synchShopcartData(usersResult.getId(),request,response);
        return IMOOCJSONResult.ok();
    }

    @ApiOperation(value = "用户登录",notes = "用户登录",httpMethod = "POST")
    @PostMapping("/login")
    public IMOOCJSONResult login(@RequestBody UserBO userBO, HttpServletRequest request, HttpServletResponse response) throws Exception {

        String username = userBO.getUsername();
        String password = userBO.getPassword();

        //1.判断用户名和密码必须不为空
        if (StringUtils.isBlank(username) || StringUtils.isBlank(password)){
            return IMOOCJSONResult.errorMsg("用户名或者密码不能为空");
        }

        //2.实现登录
        Users usersResult = userService.queryUserForLogin(username,  MD5Utils.getMD5Str(password));

        if (usersResult == null){
            return IMOOCJSONResult.errorMsg("用户名或密码不正确");
        }

        UsersVO usersVO = conventUsersVO(usersResult);

        CookieUtils.setCookie(request,response,"user", JsonUtils.objectToJson(usersVO),true);

        //生成用户token，存入redis会话
        //同步购物车数据
        synchShopcartData(usersResult.getId(),request,response);
        return IMOOCJSONResult.ok(usersResult);
    }

    /**
     * 注册登录成功后，同步cookie和redis的购物车数据
     */
    public void synchShopcartData(String userId,HttpServletRequest request,HttpServletResponse response){

        /**
         * 1.redis中无数据，如果cookie中的购物车为空，那么这个时候不做任何处理
         *              如果cookie中的购物车不为空，此时直接放入redis中
         * 2.redis中有数据，如果cookie中的购物车为空，那么直接把redis的购物车覆盖本地cookie
         *              如果cookie中的购物车不为空，如果cookie中的某个商品在redis中存在，则以cookie为主，删除redis中的，把cookie中的商品直接覆盖redis中（参考京东）
         * 3.同步到redis中去了以后，覆盖本地cookie购物车的数据，保证本地购物车的数据是最新的
         */
        //从redis中获取购物车
        String shopcartJsonRedis = redisOperator.get(FOODIE_SHOPCART + ":" + userId);
        //从cookie中获取购物车
        String shopcartStrCookie = CookieUtils.getCookieValue(request,FOODIE_SHOPCART,true);

        if (StringUtils.isBlank(shopcartJsonRedis)){
            //redis为空，cookie不为空，直接把cookie中的数据放入redis中
            if (StringUtils.isNotBlank(shopcartStrCookie)){
                redisOperator.set(FOODIE_SHOPCART + ":" + userId,shopcartStrCookie);
            }
        }else{
            //redis不为空，cookie不为空，合并cookie和redis中购物车商品数据（同一商品则覆盖redis）
            if(StringUtils.isNotBlank(shopcartStrCookie)){
                /**
                 * 1. 已经存在的，把cookie中对应的数量覆盖redis中的（参考京东）
                 * 2. 该项商品标记为待删除，统一放入一个待删除的list
                 * 3. 从cookie中清理所有的待删除list
                 * 4. 合并redis和cookie中的数据
                 * 5. 更新redis和cookie中
                 */
                List<ShopcartBo> shopcartListRedis = JsonUtils.jsonToList(shopcartJsonRedis,ShopcartBo.class);
                List<ShopcartBo> shopcartListCookie = JsonUtils.jsonToList(shopcartStrCookie, ShopcartBo.class);
                //定义一个待删除List
                List<ShopcartBo> pendingDeleteList = new ArrayList<>();
                for (ShopcartBo redisShopcart : shopcartListRedis) {
                    String redisSpecId = redisShopcart.getSpecId();

                    for (ShopcartBo cookieShopcart : shopcartListCookie) {
                        String cookieSpecId = cookieShopcart.getSpecId();

                        if(redisSpecId.equals(cookieSpecId)){
                            //覆盖购买数量，不累加，参考京东
                            redisShopcart.setBuyCounts(cookieShopcart.getBuyCounts());
                            //把cookieShopcart放入待删除列表，用于最后的删除与合并
                            pendingDeleteList.add(cookieShopcart);
                        }
                    }
                }
                //从现有cookie中删除对应的覆盖过的商品数据
                shopcartListCookie.removeAll(pendingDeleteList);
                //合并两个list
                shopcartListRedis.addAll(shopcartListCookie);
                //更新到redis和cookie
                CookieUtils.setCookie(request,response,FOODIE_SHOPCART,JsonUtils.objectToJson(shopcartListRedis),true);
                redisOperator.set(FOODIE_SHOPCART + ":" + userId,shopcartStrCookie);
            }else{
                //redis不为空，cookie为空，直接把redis覆盖cookie
                CookieUtils.setCookie(request,response,FOODIE_SHOPCART,shopcartJsonRedis,true);
            }
        }

    }

    private Users setNullProperty(Users usersResult){
        usersResult.setPassword(null);
        usersResult.setMobile(null);
        usersResult.setMobile(null);
        usersResult.setCreatedTime(null);
        usersResult.setUpdatedTime(null);
        usersResult.setBirthday(null);
        return usersResult;
    }

    @ApiOperation(value = "用户退出登录",notes = "用户退出登录",httpMethod = "POST")
    @PostMapping("/logout")
    public IMOOCJSONResult logout(@RequestParam String userId,HttpServletResponse response,HttpServletRequest request){

        //清楚用户信息相关的cookie
        CookieUtils.deleteCookie(request,response,"user");
        // 用户退出登录，清除redis中user的会话信息
        redisOperator.del(REDIS_USER_TOKEN + ":" + userId);
        //分布式会话中需要清除用户数据
        CookieUtils.deleteCookie(request,response,FOODIE_SHOPCART);
        return  IMOOCJSONResult.ok();
    }
}
