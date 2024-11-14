package net.fangyi.acmsb.mapper;

import net.fangyi.acmsb.entity.Sign;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface SignMapper {
    //查询
    @Select("select * from sign where username = #{uname}")
    Sign findByUsername(String uname);

    //注册账户
    @Insert("insert into sign(username, password, email, phone, salt)" +"values  (#{username},#{password}, #{email}, #{phone}, #{salt})")
    void signup(String username, String password, String email, String phone, String salt);

    @Update("update sign set password = #{password}, Salt = #{salt} where username = #{username}")
    void updatePassword(String username, String password, String salt);
}