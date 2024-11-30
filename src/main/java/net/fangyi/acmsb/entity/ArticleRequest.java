package net.fangyi.acmsb.entity;

import lombok.Data;

import java.util.List;

@Data
public class ArticleRequest {
    private int aid; // 文章ID
    private String title;  // 文章标题
    private String content;  // 文章内容
    private int authorId;  // 作者id
    private String createTime; //创建时间
    private String classify; //文章分类
    private List<String> tags; //文章标签
    private String status; //文章状态

    public Article toArticle() {
        Article article = new Article();
        article.setAid(aid);
        article.setTitle(title);
        article.setContent(content);
        article.setAuthorId(authorId);
        article.setCreateTime(createTime);
        article.setClassify(classify);
        article.setTags(String.valueOf(tags));
        article.setStatus(status);
        return article;
    }
}
