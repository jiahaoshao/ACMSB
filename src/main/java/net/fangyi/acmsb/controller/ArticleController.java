package net.fangyi.acmsb.controller;

import net.fangyi.acmsb.entity.Article;
import net.fangyi.acmsb.result.Result;
import net.fangyi.acmsb.service.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/articles")
public class ArticleController {

    @Autowired
    private ArticleService articleService;

    @GetMapping("/getArticles")
    public Map<String, Object> getArticles(
            @RequestParam(defaultValue = "1") int page, // 默认第一页
            @RequestParam(defaultValue = "10") int limit // 默认每页 10 条
    ) {
        // 获取分页结果
        Page<Article> articlePage = articleService.getArticles(page, limit);

        // 构建返回的响应数据
        Map<String, Object> response = new HashMap<>();
        response.put("data", articlePage.getContent()); // 当前页的数据
        response.put("currentPage", page); // 当前页
        response.put("totalPages", articlePage.getTotalPages()); // 总页数
        response.put("totalItems", articlePage.getTotalElements()); // 总数据条数
        response.put("hasMore", articlePage.hasNext()); // 是否有下一页
        return response;
    }
}
