package com.wsz.mapper;

import com.wsz.my.mapper.MyMapper;
import com.wsz.pojo.Users;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UsersMapper extends MyMapper<Users> {
}
