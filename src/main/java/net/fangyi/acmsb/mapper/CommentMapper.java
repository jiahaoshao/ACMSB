package net.fangyi.acmsb.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import net.fangyi.acmsb.entity.Comment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author Y·C
 * @version 1.0.0
 * @ClassName CommentDao.java
 * @Description TODO
 * @createTime 2023年04月21日 22:06:00
 */
@Mapper
public interface CommentMapper {

    @Select("SELECT * FROM comments WHERE foreign_id = #{foreignId}")
    List<Comment> selectList(int foreignId);

}
