package com.wsz.mapper;

import com.wsz.my.mapper.MyMapper;
import com.wsz.pojo.Items;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ItemsMapper extends MyMapper<Items> {
}
