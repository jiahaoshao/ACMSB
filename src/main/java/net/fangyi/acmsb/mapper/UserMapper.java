package net.fangyi.acmsb.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface UserMapper {
    @Update("UPDATE user SET avatar = #{avatar} WHERE uid = #{uid}")
    void updateAvatar(int uid, String avatar);
}
