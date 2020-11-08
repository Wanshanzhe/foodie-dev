package com.wsz.service;

import com.wsz.pojo.Users;
import com.wsz.pojo.bo.UserBO;

/**
 * @author by 完善者
 * @date 2020/8/5 22:20
 * @DESC
 */
public interface UserService {
    //判断用户名是否存在
    public boolean queryUsernameIsExist(String username);

    //创建用户
    public Users createUser(UserBO userBOs);

    //检索用户和密码是否匹配，用于登录
    public Users queryUserForLogin(String username,String password);
}
