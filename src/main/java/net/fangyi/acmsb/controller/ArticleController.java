package net.fangyi.acmsb.controller;

import net.fangyi.acmsb.Util.DateUtil;
import net.fangyi.acmsb.entity.Article;
import net.fangyi.acmsb.entity.ArticleRequest;
import net.fangyi.acmsb.repository.ArticleRepository;
import net.fangyi.acmsb.result.Result;
import net.fangyi.acmsb.service.ArticleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/articles")
public class ArticleController {
    private static final Logger logger = LoggerFactory.getLogger(ArticleController.class);
    @Autowired
    private ArticleService articleService;

    @Autowired
    private ArticleRepository articleRepository;

    /**
     * 获取文章列表
     * @param page
     * @param limit
     * @return
     */
    @GetMapping("/getarticles")
    public ResponseEntity<?> getArticles(
            @RequestParam(defaultValue = "1") int page, // 默认第一页
            @RequestParam(defaultValue = "10") int limit // 默认每页 10 条
    ) {
        // 获取分页结果
        Page<Article> articlePage = articleService.getArticles(page, limit);

//        if(articlePage.isEmpty())
//        {
//            for(int i = 0; i < 20; i ++)
//            {
//                Article article = new Article();
//                article.setTitle(i + "号文章");
//                article.setContent(i + "号文章的内容");
//                article.setAuthorId(i);
//                articleRepository.save(article);
//            }
//        }

        // 构建返回的响应数据
        Map<String, Object> response = new HashMap<>();
        response.put("data", articlePage.getContent()); // 当前页的数据
        response.put("currentPage", page); // 当前页
        response.put("totalPages", articlePage.getTotalPages()); // 总页数
        response.put("totalItems", articlePage.getTotalElements()); // 总数据条数
        response.put("hasMore", articlePage.hasNext()); // 是否有下一页
        return ResponseEntity.ok(response);
    }

    /**
     * 添加文章
     * @param article
     * @return
     */

    @PostMapping("/update_article")
    public ResponseEntity<?> updateArticle(@RequestBody ArticleRequest article){
        logger.info("update article: {}", article.toString());
        if(article.getAid() == -1) {
            article.setCreateTime(DateUtil.getNowTime());
             Article article1 = articleRepository.save(article.toArticle());
            return ResponseEntity.ok(Result.success("保存成功", article1));
        }
        else {
            articleService.updateArticle(article.toArticle());
        }
        return ResponseEntity.ok(Result.success("保存成功", article));
    }

    @GetMapping("/getarticlebyaid")
    public ResponseEntity<?> getArticleByAid(@RequestParam int aid){
        Article article = articleRepository.getArticleByAid(aid);
        if(article == null) {
            return ResponseEntity.ok(Result.error("文章不存在"));
        }
        return ResponseEntity.ok(Result.success("查询成功", article));
    }
}
