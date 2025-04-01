package net.fangyi.acmsb.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import net.fangyi.acmsb.Util.DateUtil;
import net.fangyi.acmsb.Util.UploadFileUtil;
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
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@RestController
@RequestMapping("/articles")
@Tag(name = "文章控制器", description = "用于管理文章的接口")
public class ArticleController {
    private static final Logger logger = LoggerFactory.getLogger(ArticleController.class);
    @Autowired
    private ArticleService articleService;
    @Autowired
    private ArticleRepository articleRepository;

    public static final int IMAGE_MAX_SIZE = 5 * 1024 * 1024;
    /* 限制上传文件的类型 */
    public static final List<String> IMAGE_TYPE = new ArrayList<>();
    static { // 初始化
        IMAGE_TYPE.add("image/jpeg");
        IMAGE_TYPE.add("image/png");
        IMAGE_TYPE.add("image/bmp");
        IMAGE_TYPE.add("image/gif");
    }

    /**
     * 获取文章列表
     * @param page 当前页码
     * @param limit 每页数量
     * @param status 文章状态
     * @return 分页的文章列表
     */
    @Operation(summary = "获取文章列表", description = "根据分页参数和状态获取文章列表")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "成功获取文章列表",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Result.class))),
            @ApiResponse(responseCode = "400", description = "请求参数错误")
    })
    @GetMapping("/getarticles")
    public ResponseEntity<?> getArticles(
            @Parameter(description = "当前页码，默认为1") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页数量，默认为10") @RequestParam(defaultValue = "10") int limit,
            @Parameter(description = "文章状态，默认为'已发布'") @RequestParam(defaultValue = "已发布") String status) {
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
     * 添加或更新文章
     * @param articleRequest 文章信息
     * @return 操作结果
     */
    @Operation(summary = "添加或更新文章", description = "根据提供的文章信息添加或更新文章")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "成功添加或更新文章",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Result.class))),
            @ApiResponse(responseCode = "400", description = "请求体错误")
    })
    @PostMapping("/update_article")
    public ResponseEntity<?> updateArticle(@RequestBody @Parameter(description = "文章信息") ArticleRequest articleRequest) {
        logger.info("update article: {}", articleRequest.toString());
        if(articleRequest.getAid() == -1) {
            articleRequest.setCreateTime(DateUtil.getNowTime());
            Article article = articleRepository.save(articleRequest.toArticle());
            return ResponseEntity.ok(Result.success("保存成功", article));
        } else {
            articleService.updateArticle(articleRequest.toArticle());
        }
        return ResponseEntity.ok(Result.success("保存成功", articleRequest));
    }

    /**
     * 根据ID获取文章
     * @param aid 文章ID
     * @return 文章详情
     */
    @Operation(summary = "根据ID获取文章", description = "根据文章ID获取文章详情")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "成功获取文章",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Result.class))),
            @ApiResponse(responseCode = "404", description = "文章不存在")
    })
    @GetMapping("/getarticlebyaid")
    public ResponseEntity<?> getArticleByAid(@Parameter(description = "文章ID") @RequestParam int aid) {
        Article article = articleRepository.getArticleByAid(aid);
        if(article == null) {
            return ResponseEntity.ok(Result.error("文章不存在"));
        }
        return ResponseEntity.ok(Result.success("查询成功", article));
    }

    /**
     * 搜索文章
     * @param keyword 搜索关键字
     * @return 搜索结果
     */
    @Operation(summary = "搜索文章", description = "根据关键字搜索文章")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "成功获取搜索结果",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Result.class))),
            @ApiResponse(responseCode = "400", description = "请求参数错误")
    })
    @GetMapping("/search")
    public ResponseEntity<?> search(@Parameter(description = "搜索关键字") @RequestParam String keyword) {
        List<Article> articles = articleRepository.findAllByTitleLike("%" + keyword + "%");
        List<Article> articles1 = articleRepository.findAllByContentLike("%" + keyword + "%");

        Set<Article> combinedArticles = new HashSet<>(articles);
        combinedArticles.addAll(articles1);

        combinedArticles.removeIf(article -> article.getStatus().equals("草稿"));

        return ResponseEntity.ok(new ArrayList<>(combinedArticles));
    }

    /**
     * 上传文章图片
     */
    @Operation(summary = "上传文章图片", description = "上传文章图片")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "上传成功",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Result.class))),
            @ApiResponse(responseCode = "400", description = "请求参数错误")
    })
    @PostMapping("/upload_article_images")
    public ResponseEntity<?> uploadArticleImages(@Parameter(description = "文件对象数组") @RequestParam("files") MultipartFile multipartFile) throws IOException {
        if (multipartFile.isEmpty()) {
            return ResponseEntity.ok(Result.error("文件为空"));
        }
        if (multipartFile.getSize() > IMAGE_MAX_SIZE) {
            return ResponseEntity.ok(Result.error("文件大小超出限制"));
        }
        String contentType = multipartFile.getContentType();
        if (!IMAGE_TYPE.contains(contentType)) {
            return ResponseEntity.ok(Result.error("文件类型不支持"));
        }
        String articleImageUrl = UploadFileUtil.upload(multipartFile, "static/images/article_image");
        if (articleImageUrl == null) {
            return ResponseEntity.ok(Result.error("上传失败"));
        }
        return ResponseEntity.ok(Result.success("上传成功", articleImageUrl));
    }
}