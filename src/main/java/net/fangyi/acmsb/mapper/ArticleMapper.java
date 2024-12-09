package net.fangyi.acmsb.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;
import org.springframework.context.annotation.Bean;

@Mapper
public interface ArticleMapper {

    @Update("update article set title = #{title}, content = #{content}, authorid = #{authorid}, create_time = #{createTime}, classify = #{classify}, tags = #{tags}, status = #{status} where aid = #{aid}")
    void updateArticle(int aid, String title, String content, int authorid, String createTime, String classify, String tags, String status);
}
