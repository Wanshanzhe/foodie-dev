package com.wsz.mapper;

import com.wsz.my.mapper.MyMapper;
import com.wsz.pojo.Category;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CategoryMapper extends MyMapper<Category> {
}
