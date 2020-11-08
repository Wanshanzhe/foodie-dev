package com.wsz.controller;

import com.wsz.pojo.Users;
import com.wsz.pojo.bo.UserBO;
import com.wsz.service.UserService;
import com.wsz.utils.CookieUtils;
import com.wsz.utils.IMOOCJSONResult;
import com.wsz.utils.JsonUtils;
import com.wsz.utils.MD5Utils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author by 完善者
 * @date 2020/8/7 23:33
 * @DESC
 */
@Api(value = "注册登录",tags = {"用于注册登录的相关接口"})
@RestController
@RequestMapping("passport")
public class PassportController {

    @Autowired
    private UserService userService;

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

        usersResult = setNullProperty(usersResult);
        CookieUtils.setCookie(request,response,"user", JsonUtils.objectToJson(usersResult),true);

        //TODO 生成用户token，存入redis会话
        //TODO 同步购物车数据

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
        usersResult = setNullProperty(usersResult);
        CookieUtils.setCookie(request,response,"user", JsonUtils.objectToJson(usersResult),true);

        //TODO 生成用户token，存入redis会话
        //TODO 同步购物车数据

        return IMOOCJSONResult.ok(usersResult);
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
        // TODO 用户退出登录需要清空购物车
        // TODO 分布式会话中需要清除用户数据
        //如果退出成功
        return  IMOOCJSONResult.ok();
    }
}
