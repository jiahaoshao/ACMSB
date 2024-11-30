package net.fangyi.acmsb.service;


import net.fangyi.acmsb.entity.Article;
import org.springframework.data.domain.Page;

public interface ArticleService {
    Page<Article> getArticles(int page, int limit);

    void updateArticle(Article article);
}