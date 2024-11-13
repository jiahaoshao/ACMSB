package net.fangyi.acmsb.mapper;

import net.fangyi.acmsb.entity.Sign;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface SignMapper {
    //查询
    @Select("select * from sign where username = #{uname}")
    Sign findByUsername(String uname);
    //新增
    @Insert("insert into sign(username, password)" +"values  (#{uname},#{psw})")
    void add(String uname, String psw);
}