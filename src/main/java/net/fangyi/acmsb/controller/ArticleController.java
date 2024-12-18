package net.fangyi.acmsb.controller;

import net.fangyi.acmsb.Util.DateUtil;
import net.fangyi.acmsb.entity.Article;
import net.fangyi.acmsb.entity.ArticleRequest;
import net.fangyi.acmsb.entity.Comment;
import net.fangyi.acmsb.repository.ArticleRepository;
import net.fangyi.acmsb.result.Result;
import net.fangyi.acmsb.service.ArticleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/articles")
public class ArticleController {
    private static final Logger logger = LoggerFactory.getLogger(ArticleController.class);
    @Autowired
    private ArticleService articleService;

    @Autowired
    private ArticleRepository articleRepository;

    //@Autowired
    //private CommentRepository commentRepository;
    /**
     * 获取文章列表
     * @param page
     * @param limit
     * @return
     */
    @GetMapping("/getarticles")
    public ResponseEntity<?> getArticles(
            @RequestParam(defaultValue = "1") int page, // 默认第一页
            @RequestParam(defaultValue = "10") int limit, // 默认每页 10 条
            @RequestParam(defaultValue = "已发布") String status
    ) {
        // 获取分页结果
        logger.info("get articles: page = {}, limit = {}, status = {}", page, limit, status);

        Page<Article> articlePage = articleService.getArticles(page, limit, status);

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
    public ResponseEntity<?> getArticleByAid(@RequestParam("aid") int aid){
        Article article = articleRepository.getArticleByAid(aid);
        if(article == null) {
            return ResponseEntity.ok(Result.error("文章不存在"));
        }
        return ResponseEntity.ok(Result.success("查询成功", article));
    }

//    @GetMapping("/getcommentbyparentid")
//    public ResponseEntity<?> getCommentByParentId(@RequestParam int parentid){
//        List<Comment> comments = commentRepository.findAllByParentid(parentid);
//        return ResponseEntity.ok(comments);
//    }
//
//    @PostMapping("/addcomment")
//    public ResponseEntity<?> addComment(@RequestBody Comment comment){
//        comment.setCreateTime(DateUtil.getNowTime());
//        commentRepository.save(comment);
//        return ResponseEntity.ok(Result.success("评论成功", comment));
//    }

    @GetMapping("/search")
    public ResponseEntity<?> search(@RequestParam String keyword){
        List<Article> articles = articleRepository.findAllByTitleLike("%" + keyword + "%");
        List<Article> articles1 = articleRepository.findAllByContentLike("%" + keyword + "%");

        Set<Article> combinedArticles = new HashSet<>(articles);
        combinedArticles.addAll(articles1);

        combinedArticles.removeIf(article -> article.getStatus().equals("草稿"));

        return ResponseEntity.ok(new ArrayList<>(combinedArticles));
    }
}
