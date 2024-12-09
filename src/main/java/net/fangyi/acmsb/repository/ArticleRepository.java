package net.fangyi.acmsb.repository;

import net.fangyi.acmsb.entity.Article;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ArticleRepository extends JpaRepository<Article, Integer> {
    Article getArticleByAid(int aid);
    // 这里不需要手动写分页查询方法，JPA会自动处理
    Page<Article> findByStatus(String status, Pageable pageable);
}