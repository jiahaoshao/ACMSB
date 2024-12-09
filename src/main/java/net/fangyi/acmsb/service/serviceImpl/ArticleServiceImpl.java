package net.fangyi.acmsb.service.serviceImpl;

import net.fangyi.acmsb.entity.Article;
import net.fangyi.acmsb.mapper.ArticleMapper;
import net.fangyi.acmsb.repository.ArticleRepository;
import net.fangyi.acmsb.service.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
@Service
public class ArticleServiceImpl implements ArticleService {

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private ArticleMapper articleMapper;

    public Page<Article> getArticles(int page, int limit, String status) {
        // 创建分页请求对象，page - 1 因为分页从 0 开始
        Pageable pageable = PageRequest.of(page - 1, limit);
        return articleRepository.findByStatus(status, pageable);
    }

    // 更新文章
    @Override
    public void updateArticle(Article article) {
        articleMapper.updateArticle(article.getAid(), article.getTitle(), article.getContent(), article.getAuthorId(), article.getCreateTime(), article.getClassify(), article.getTags(), article.getStatus());
    }
}