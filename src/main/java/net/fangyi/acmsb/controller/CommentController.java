package net.fangyi.acmsb.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import net.fangyi.acmsb.Util.DateUtil;
import net.fangyi.acmsb.entity.Comment;
import net.fangyi.acmsb.repository.CommentRepository;
import net.fangyi.acmsb.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@CrossOrigin
@RestController
@RequestMapping("/comment")
@Tag(name = "评论控制器", description = "用于管理评论的接口")
public class CommentController {

    @Resource
    private CommentService commentService;

    @Autowired
    private CommentRepository commentRepository;

    @Operation(summary = "保存评论", description = "创建一个新的评论")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "评论保存成功"),
            @ApiResponse(responseCode = "400", description = "请求参数错误")
    })
    @PostMapping("/save")
    public void save(@RequestBody @Parameter(description = "评论内容") Comment comment){
        comment.setCreateTime(DateUtil.getNowTime());
        commentRepository.save(comment);
    }

    @Operation(summary = "获取评论列表", description = "根据外键ID获取评论列表及评分")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "成功获取评论列表",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class))),
            @ApiResponse(responseCode = "400", description = "请求参数错误")
    })
    @GetMapping("/list")
    public Map<String,Object> list(@Parameter(description = "外键ID") @RequestParam Integer foreignId){

        Map<String,Object> map = new HashMap<>();
        map.put("rate",BigDecimal.ZERO);

        List<Comment> comments = commentRepository.findAllByForeignId(foreignId);

        //获得所有不为空的评分
        List<Comment> commentList = comments.stream().filter(comment -> comment.getRate() != null).collect(Collectors.toList());
        //将查询出来的评分进行累加
        commentList.stream().map(Comment::getRate).reduce(BigDecimal::add).ifPresent(res -> {
            //算出平均分
            map.put("rate",res.divide(BigDecimal.valueOf(commentList.size()),1,RoundingMode.HALF_UP));
        });

        List<Comment> rootComments = comments.stream().filter(comment -> comment.getPid() == null).collect(Collectors.toList());
        for (Comment rootComment : rootComments) {
            rootComment.setChildren(comments.stream().filter(comment -> rootComment.getId().equals(comment.getPid())).collect(Collectors.toList()));
        }

        map.put("comments",rootComments);

        return map;
    }
}