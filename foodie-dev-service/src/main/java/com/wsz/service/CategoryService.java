package com.wsz.service;

import com.wsz.pojo.Category;
import com.wsz.pojo.vo.CategoryVO;
import com.wsz.pojo.vo.NewItemsVO;

import java.util.List;

/**
 * @author by 完善者
 * @date 2020/9/20 11:00
 * @DESC
 */
public interface CategoryService {
    /**
     * 查询所有一级分类
     * @return
     */
    public List<Category> queryAllRootLevelCat();

    /**
     * 根据一级分类id查询子分类
     * @param rootCatId
     * @return
     */
    public List<CategoryVO> getSubCatList(Integer rootCatId);


    /**
     * 查询首页每个一级分类下的6条最新商品数据
     * @param rootCatId
     * @return
     */
    public List<NewItemsVO> getSixNewItemsLazy(Integer rootCatId);

}
