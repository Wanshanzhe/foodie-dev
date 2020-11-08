package com.wsz.service;

import com.wsz.pojo.Carousel;

import java.util.List;

/**
 * @author by 完善者
 * @date 2020/9/20 9:29
 * @DESC
 */
public interface CarouseService {

    /**
     * 查询所有轮播图列表
     * @param isShow
     * @return
     */
    public List<Carousel> queryAll(Integer isShow);
}
