package net.fangyi.acmsb.entity;


import com.alibaba.fastjson2.JSONArray;
import com.google.gson.Gson;
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
        Gson gson = new Gson();
        Article article = new Article();
        article.setAid(aid);
        article.setTitle(title);
        article.setContent(content);
        article.setAuthorId(authorId);
        article.setCreateTime(createTime);
        article.setClassify(classify);
        article.setTags(gson.toJson(tags));
        article.setStatus(status);
        return article;
    }
}
