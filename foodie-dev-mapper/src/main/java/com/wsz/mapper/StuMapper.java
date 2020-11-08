package com.wsz.mapper;

import com.wsz.my.mapper.MyMapper;
import com.wsz.pojo.Stu;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface StuMapper extends MyMapper<Stu> {
}
