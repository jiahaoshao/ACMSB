package net.fangyi.acmsb.mapper;

import net.fangyi.acmsb.entity.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper {
    //查询
    @Select("select * from user where username = #{uname}")
    User findByUser(String uname);
    //新增
    @Insert("insert into user(username, password)" +"values  (#{uname},#{psw})")
    void add(String uname, String psw);
}