package net.fangyi.acmsb.mapper;

import net.fangyi.acmsb.entity.Sign;
import org.apache.ibatis.annotations.*;

@Mapper
public interface SignMapper {

    @Update("update sign set password = #{password}, Salt = #{salt} where username = #{username}")
    void updatePassword(String username, String password, String salt);
}