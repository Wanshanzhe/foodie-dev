package com.wsz.service.center;

import com.wsz.pojo.Users;
import com.wsz.pojo.bo.center.CenterUserBO;

/**
 * @author by 完善者
 * @date 2020/10/8 9:27
 * @DESC 用户中心的service
 */
public interface CenterUserService {
    /**
     * 根据用户id查询用户信息
     * @param userId
     * @return
     */
    public Users queryUserInfo(String userId);

    /**
     * 修改用户信息
     * @param userId
     * @param centerUserBO
     */
    public Users updateUserInfo(String userId, CenterUserBO centerUserBO);

    /**
     * 用户头像更新
     * @param userId
     * @param faceUrl
     * @return
     */
    public Users updateUserFace(String userId,String faceUrl);
}
