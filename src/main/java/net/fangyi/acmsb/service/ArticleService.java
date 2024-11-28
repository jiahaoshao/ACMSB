package net.fangyi.acmsb.service;

import net.fangyi.acmsb.entity.Article;
import net.fangyi.acmsb.mapper.ArticleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
@Service
public class ArticleService {

    @Autowired
    private ArticleRepository articleRepository;

    public Page<Article> getArticles(int page, int limit) {
        // 创建分页请求对象，page - 1 因为分页从 0 开始
        Pageable pageable = PageRequest.of(page - 1, limit);
        return articleRepository.findAll(pageable);
    }
}