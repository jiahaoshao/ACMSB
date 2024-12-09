package net.fangyi.acmsb.controller;


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

/**
 * @author Y·C
 * @version 1.0.0
 * @ClassName CommentController.java
 * @Description TODO
 * @createTime 2023年04月21日 22:09:00
 */
@CrossOrigin
@RestController
@RequestMapping("/comment")
public class CommentController {

    @Resource
    private CommentService commentService;

    @Autowired
    private CommentRepository commentRepository;

    @PostMapping("/save")
    public void save(@RequestBody Comment comment){
        comment.setCreateTime(DateUtil.getNowTime());
        commentRepository.save(comment);
    }

    @GetMapping("/list")
    public Map<String,Object> list(@RequestParam Integer foreignId){

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
