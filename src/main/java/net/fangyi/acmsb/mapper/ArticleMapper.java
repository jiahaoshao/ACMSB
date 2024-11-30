package net.fangyi.acmsb.mapper;

import net.fangyi.acmsb.entity.Article;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface ArticleMapper {
    @Select("select *from article where aid=#{id} ")
    Article getArticleById(int id);

    @Update("update article set title = #{title}, content = #{content}, authorid = #{authorid}, create_time = #{createTime}, classify = #{classify}, tags = #{tags}, status = #{status} where aid = #{aid}")
    void updateArticle(int aid, String title, String content, int authorid, String createTime, String classify, String tags, String status);
}
