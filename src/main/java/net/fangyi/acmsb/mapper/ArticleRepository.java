package net.fangyi.acmsb.mapper;

import net.fangyi.acmsb.entity.Article;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ArticleRepository extends JpaRepository<Article, Long> {
    // 这里不需要手动写分页查询方法，JPA会自动处理
}